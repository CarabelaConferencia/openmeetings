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
package org.apache.openmeetings.conference.room;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;

public interface IClientList {

	/**
	 * Get current clients and extends the room client with its potential
	 * audio/video client and settings
	 * 
	 * @param room_id
	 * @param server TODO
	 * @return
	 */
	public abstract RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl, boolean isAVClient);

	public abstract Collection<RoomClient> getAllClients();

	/**
	 * Get a client by its streamId
	 * 
	 * @param streamId
	 * @param server
	 *            TODO
	 * @return
	 */
	public abstract RoomClient getClientByStreamId(String streamId,
			Server server);

	/**
	 * Additionally checks if the client receives sync events
	 * 
	 * Sync events will no be broadcasted to: - Screensharing users -
	 * Audio/Video connections only
	 * 
	 * @param streamId
	 * @return
	 */
	public abstract RoomClient getSyncClientByStreamId(String streamId);

	public abstract RoomClient getClientByPublicSID(String publicSID,
			boolean isAVClient, Server server);

	/**
	 * 
	 * @param userId
	 * @return
	 * 
	 * @deprecated There could be multiple users logged in with the same userid,
	 *             then this call would return a list not a single user
	 */
	public abstract RoomClient getClientByUserId(Long userId);

	/**
	 * Update the session object of the audio/video-connection and additionally
	 * swap the values to the session object of the user that holds the full
	 * session object
	 * 
	 * @param streamId
	 * @param rcm
	 * @return
	 */
	public abstract Boolean updateAVClientByStreamId(String streamId,
			RoomClient rcm);

	/**
	 * Update the session object
	 * 
	 * updateRoomCount is only <i>one</i> time true, in
	 * {@link ScopeApplicationAdapter#setRoomValues(Long, Boolean, Boolean, Long, String)}
	 * .
	 * 
	 * @param streamId
	 * @param rcm
	 * @param updateRoomCount
	 *            true means the count for the room has to be updated
	 * @return
	 */
	public abstract Boolean updateClientByStreamId(String streamId,
			RoomClient rcm, boolean updateRoomCount);

	/**
	 * Remove a client from the session store
	 * 
	 * @param streamId
	 * @return
	 */
	public abstract Boolean removeClient(String streamId);

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 * @param room_id 
	 * @return
	 */
	public abstract List<RoomClient> getClientListByRoom(Long room_id, Server server);

	public abstract Collection<RoomClient> getClientListByRoomAll(Long room_id, Server server);

	/**
	 * get the current Moderator in this room
	 * 
	 * @param roomname
	 * @return
	 */
	public abstract List<RoomClient> getCurrentModeratorByRoom(Long room_id);

	/**
	 * Get list of current client sessions
	 * 
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public abstract SearchResult<ClientSession> getListByStartAndMax(int start,
			int max, String orderby, boolean asc);

	/**
	 * returns number of current users recording
	 * 
	 * @param roomId
	 * @return
	 */
	public abstract long getRecordingCount(long roomId);

	/**
	 * returns a number of current users publishing screensharing
	 * 
	 * @param roomId
	 * @return
	 */
	public abstract long getPublishingCount(long roomId);
	
	/**
	 * Get a list of all servers of all rooms on that server, serverId = null 
	 * means it is a local session on the master.
	 * 
	 * @param server
	 * @return
	 */
	public abstract Set<Long> getActiveRoomIdsByServer(Server server);

}