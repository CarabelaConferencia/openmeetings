/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.rooms.Client;
import org.apache.openmeetings.session.store.HashMapStore;
import org.apache.openmeetings.session.store.IClientPersistenceStore;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User an in-memory HashMap to store the current sessions.
 * 
 * @author sebawagner
 * 
 */
public class SessionManager implements ISessionManager {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			SessionManager.class, OpenmeetingsVariables.webAppRootKey);
	
	protected static IClientPersistenceStore cache = new HashMapStore();
	
	private static Set<Long> EMPTY_HASH_SET = new HashSet<Long>();

	@Autowired
	private ManageCryptStyle manageCryptStyle;
	
	public synchronized Client addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl, boolean isAVClient) {
		try {

			// Store the Connection into a bean and add it to the HashMap
			Client rcm = new Client();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope(scopeName);
			long random = System.currentTimeMillis() + new BigInteger(256, new Random()).longValue();
			
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(random).toString()));

			rcm.setUserport(remotePort);
			rcm.setUserip(remoteAddress);
			rcm.setSwfurl(swfUrl);
			rcm.setIsMod(new Boolean(false));
			rcm.setCanDraw(new Boolean(false));
			rcm.setIsAVClient(isAVClient);

			if (cache.containsKey(null, streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			cache.put(null, rcm.getStreamid(), rcm);

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	public synchronized Collection<Client> getAllClients() {
		HashMap<String, Client> clients = cache.getClientsByServer(null);
		if (clients == null) {
			return new ArrayList<Client>(0);
		}
		return clients.values();
	}

	public synchronized Client getClientByStreamId(String streamId, Server server) {
		try {
			if (!cache.containsKey(server, streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return cache.get(server, streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Client getSyncClientByStreamId(String streamId) {
		try {
			if (!cache.containsKey(null, streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}

			Client rcl = cache.get(null, streamId);

			if (rcl == null) {
				return null;
			}

			if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				return null;
			}

			return cache.get(null, streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public Client getClientByPublicSID(String publicSID, boolean isAVClient, Server server) {
		try {
			for (Client rcl : cache.getClientsByPublicSID(server, publicSID)) {
				if (rcl.getIsAVClient() != isAVClient) {
					continue;
				}
				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}
	
	public ClientSessionInfo getClientByPublicSIDAnyServer(String publicSID, boolean isAVClient) {
		try {
			for (Entry<Long,List<Client>> entry : cache.getClientsByPublicSID(publicSID).entrySet()) {
				for (Client rcl : entry.getValue()) {
					if (rcl.getIsAVClient() != isAVClient) {
						continue;
					}
					return new ClientSessionInfo(rcl, entry.getKey());
				}
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSIDAnyServer]", err);
		}
		return null;
	}

	public synchronized Client getClientByUserId(Long userId) {
		try {
			for (Client rcl : cache.getClientsByUserId(null, userId)) {
				
				if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
					continue;
				}
				
				if (rcl.getIsAVClient()) {
					continue;
				}
				
				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByUserId]", err);
		}
		return null;
	}

	public synchronized Boolean updateAVClientByStreamId(String streamId,
			Client rcm) {
		try {

			// get the corresponding user session object and update the settings
			Client rclUsual = getClientByPublicSID(rcm.getPublicSID(),
					false, null);
			if (rclUsual != null) {
				rclUsual.setBroadCastID(rcm.getBroadCastID());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setVHeight(rcm.getVHeight());
				rclUsual.setVWidth(rcm.getVWidth());
				rclUsual.setVX(rcm.getVX());
				rclUsual.setVY(rcm.getVY());
				Client rclSaved = cache.get(null, rclUsual.getStreamid());
				if (rclSaved != null) {
					cache.put(null,rclUsual.getStreamid(), rclUsual);
				} else {
					log.debug("Tried to update a non existing Client "
							+ rclUsual.getStreamid());
				}
			}

			updateClientByStreamId(streamId, rcm, false);
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean updateClientByStreamId(String streamId,
			Client rcm, boolean updateRoomCount) {
		try {
			
			Client rclSaved = cache.get(null, streamId);
			
			if (rclSaved != null) {
				cache.put(null, streamId, rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[updateClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean removeClient(String streamId) {
		try {
			if (cache.containsKey(null,streamId)) {
				cache.remove(null,streamId);
				return true;
			} else {
				log.debug("Tried to remove a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return null;
	}

	public synchronized ArrayList<Client> getClientListByRoom(Long roomId, Server server) {
		ArrayList<Client> roomClientList = new ArrayList<Client>();
		try {

			for (Client rcl : cache.getClientsByRoomId(server, roomId).values()) {

				if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
					continue;
				}
				if (rcl.getIsAVClient()) {
					continue;
				}

				// Only parse really those users out that are really a full session object
				// and no pseudo session object like the audio/video or screen
				// sharing connection
				roomClientList.add(rcl);

			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}
	
	public List<Client> getClientSessionListByRoom(Long roomId,
			Server server) {
		ArrayList<Client> roomClientList = new ArrayList<Client>();
		try {

			for (Client rcl : cache.getClientsByRoomId(server, roomId).values()) {

				if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
					continue;
				}
				if (rcl.getIsAVClient()) {
					continue;
				}

				// Only parse really those users out that are really a full session object
				// and no pseudo session object like the audio/video or screen
				// sharing connection
				roomClientList.add(rcl);

			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}


	public synchronized Collection<Client> getClientListByRoomAll(Long roomId, Server server) {
		try {
			return cache.getClientsByRoomId(server, roomId).values();
		} catch (Exception err) {
			log.error("[getClientListByRoomAll]", err);
		}
		return null;
	}

	public synchronized List<Client> getCurrentModeratorByRoom(Long room_id) {
		List<Client> rclList = new LinkedList<Client>();
		List<Client> currentClients = this.getClientListByRoom(room_id, null);
		for (Client rcl : currentClients) {
			if (rcl.getIsMod()) {
				rclList.add(rcl);
			}
		}

		return rclList;
	}

	// FIXME not sorted
	public synchronized SearchResult<ServerSession> getListByStartAndMax(
			int start, int max, String orderby, boolean asc) {
		SearchResult<ServerSession> sResult = new SearchResult<ServerSession>();
		sResult.setObjectName(Client.class.getName());
		sResult.setRecords(Long.valueOf(cache.size()).longValue());
		ArrayList<ServerSession> myList = new ArrayList<ServerSession>(cache.size());
		
		//FIXME: Improve the handling of the Arrays/Map/List so that this re-parsing is not needed
		for (Entry<Long, LinkedHashMap<String, Client>> entry : cache.values().entrySet()) {
			for (Client rcl : entry.getValue().values()) {
				myList.add(new ServerSession(entry.getKey(), rcl));
			}
		}
		
		sResult.setResult(myList);
		return sResult;
	}

	public long getRecordingCount(long roomId) {
		List<Client> currentClients = this.getClientListByRoom(roomId, null);
		int numberOfRecordingUsers = 0;
		for (Client rcl : currentClients) {
			if (rcl.isStartRecording()) {
				numberOfRecordingUsers++;
			}
		}
		return numberOfRecordingUsers;
	}

	public long getPublishingCount(long roomId) {
		List<Client> currentClients = this.getClientListByRoom(roomId, null);
		int numberOfPublishingUsers = 0;
		for (Client rcl : currentClients) {
			if (rcl.isStreamPublishStarted()) {
				numberOfPublishingUsers++;
			}
		}
		return numberOfPublishingUsers;
	}
	
	public Set<Long> getActiveRoomIdsByServer(Server server) {
		if (cache.getClientsByServerAndRoom(server) == null) {
			return EMPTY_HASH_SET;
		}
		return cache.getClientsByServerAndRoom(server).keySet();
	}
	
	
	public String getSessionStatistics() {
		return cache.getDebugInformation(Arrays.asList(IClientPersistenceStore.DEBUG_DETAILS.SIZE,
						IClientPersistenceStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						IClientPersistenceStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						IClientPersistenceStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						IClientPersistenceStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));
	}

}
