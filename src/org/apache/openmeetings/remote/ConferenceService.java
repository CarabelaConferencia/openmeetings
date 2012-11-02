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
package org.apache.openmeetings.remote;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.data.basic.AuthLevelmanagement;
import org.apache.openmeetings.data.basic.Sessionmanagement;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.data.conference.RoomDAO;
import org.apache.openmeetings.data.conference.Roommanagement;
import org.apache.openmeetings.data.conference.dao.RoomModeratorsDao;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.rooms.RoomModerators;
import org.apache.openmeetings.persistence.beans.rooms.RoomTypes;
import org.apache.openmeetings.persistence.beans.rooms.Rooms;
import org.apache.openmeetings.persistence.beans.rooms.Rooms_Organisation;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.remote.red5.IClientList;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.utils.math.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ConferenceService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConferenceService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private RoomDAO roomDao;
	@Autowired
	private RoomModeratorsDao roomModeratorsDao;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private IClientList clientListManager = null;
	@Autowired
	private ServerDao serverDao;

	/**
	 * ( get a List of all availible Rooms of this organisation
	 * (non-appointments)
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public List<Rooms_Organisation> getRoomsByOrganisationAndType(String SID,
			long organisation_id, long roomtypes_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<Rooms_Organisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationIdAndRoomType(
							user_level, organisation_id, roomtypes_id);

			List<Rooms_Organisation> filtered = new ArrayList<Rooms_Organisation>();

			for (Iterator<Rooms_Organisation> iter = roomOrgsList.iterator(); iter
					.hasNext();) {
				Rooms_Organisation orgRoom = iter.next();

				if (!orgRoom.getRoom().getAppointment()) {
					orgRoom.getRoom().setCurrentusers(
							this.getRoomClientsListByRoomId(orgRoom.getRoom()
									.getRooms_id()));
					filtered.add(orgRoom);
				}
			}
			return filtered;
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<Rooms_Organisation> getRoomsByOrganisationWithoutType(
			String SID, long organisation_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<Rooms_Organisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationId(user_level,
							organisation_id);
			
			for (Rooms_Organisation roomOrg : roomOrgsList) {
				roomOrg.getRoom().setCurrentusers(clientListManager.getClientListByRoom(roomOrg.getRoom().getRooms_id()));
			}

			return roomOrgsList;
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	/**
	 * gets all rooms of an organization TODO:check if the requesting user is
	 * also member of that organization
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public SearchResult<Rooms_Organisation> getRoomsByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {

		log.debug("getRoomsByOrganisation");

		Long user_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByIdAndOrg(user_id,
				organisation_id);

		return roommanagement.getRoomsOrganisationByOrganisationId(user_level,
				organisation_id, start, max, orderby, asc);
	}

	/**
	 * get a List of all public availible rooms (non-appointments)
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public List<Rooms> getRoomsPublic(String SID, Long roomtypes_id) {
		try {
			log.debug("getRoomsPublic");

			Long users_id = sessionManagement.checkSession(SID);
			Long User_level = userManagement.getUserLevelByID(users_id);
			log.error("getRoomsPublic user_level: " + User_level);

			List<Rooms> roomList = roommanagement.getPublicRooms(User_level,
					roomtypes_id);

			// Filter : no appointed meetings
			List<Rooms> filtered = new ArrayList<Rooms>();

			for (Iterator<Rooms> iter = roomList.iterator(); iter.hasNext();) {
				Rooms rooms = iter.next();

				if (!rooms.getAppointment()) {
					rooms.setCurrentusers(this.getRoomClientsListByRoomId(rooms
							.getRooms_id()));
					filtered.add(rooms);
				}
			}

			return filtered;
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<Rooms> getRoomsPublicWithoutType(String SID) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			log.debug("getRoomsPublic user_level: " + user_level);

			List<Rooms> roomList = roomDao.getPublicRooms();
			
			for (Rooms room : roomList) {
				room.setCurrentusers(clientListManager.getClientListByRoom(room.getRooms_id()));
			}

			return roomList;
		} catch (Exception err) {
			log.error("[getRoomsPublicWithoutType]", err);
		}
		return null;
	}

	/**
	 * retrieving ServerTime
	 * 
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Date getServerTime() {
		log.debug("getServerTime");

		return new Date(System.currentTimeMillis());

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 
	 * retrieving Appointment for Room
	 * 
	 * @author o.becherer
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public Appointment getAppointMentDataForRoom(Long room_id) {
		log.debug("getAppointMentDataForRoom");

		Rooms room = roomDao.get(room_id);

		if (room.getAppointment() == false)
			return null;

		try {
			Appointment ment = appointmentLogic.getAppointmentByRoom(room_id);

			return ment;
		} catch (Exception e) {
			log.error("getAppointMentDataForRoom " + e.getMessage());
			return null;
		}

	}

	// --------------------------------------------------------------------------------------------

	public Map<String, Object> getAppointMentAndTimeZones(Long room_id) {
		try {
			log.debug("getAppointMentDataForRoom");
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
	
			log.debug("getCurrentRoomClient -2- " + streamid);
	
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);
	
			Rooms room = roomDao.get(room_id);
	
			if (room.getAppointment() == false) {
				throw new IllegalStateException("Room has no appointment");
			}
		
			Appointment appointment = appointmentLogic
					.getAppointmentByRoom(room_id);

			Map<String, Object> returnMap = new HashMap<String, Object>();

			returnMap.put("appointment", appointment);

			Users us = userManagement.getUserById(currentClient.getUser_id());
			TimeZone timezone = timezoneUtil.getTimezoneByUser(us);

			returnMap.put("appointment", appointment);

			returnMap.put(
					"start",
					CalendarPatterns.getDateWithTimeByMiliSeconds(
							appointment.getAppointmentStarttime(), timezone));
			returnMap.put(
					"end",
					CalendarPatterns.getDateWithTimeByMiliSeconds(
							appointment.getAppointmentEndtime(), timezone));
			returnMap.put("timeZone", timezone.getDisplayName());

			return returnMap;
		} catch (Exception e) {
			log.error("getAppointMentAndTimeZones " , e );
			return null;
		}

	}

	/**
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public List<Rooms> getAppointedMeetings(String SID, Long room_types_id) {
		log.debug("ConferenceService.getAppointedMeetings");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkUserLevel(user_level)) {

			List<Appointment> points = appointmentLogic
					.getTodaysAppointmentsForUser(users_id);
			List<Rooms> result = new ArrayList<Rooms>();

			if (points != null) {
				for (int i = 0; i < points.size(); i++) {
					Appointment ment = points.get(i);

					Long rooms_id = ment.getRoom().getRooms_id();
					Rooms rooom = roomDao.get(rooms_id);

					if (!rooom.getRoomtype().getRoomtypes_id()
							.equals(room_types_id))
						continue;

					rooom.setCurrentusers(getRoomClientsListByRoomId(rooom
							.getRooms_id()));
					result.add(rooom);
				}
			}

			log.debug("Found " + result.size() + " rooms");
			return result;

		} else {
			return null;
		}

	}

	// --------------------------------------------------------------------------------------------

	public List<Rooms> getAppointedMeetingRoomsWithoutType(String SID) {
		log.debug("ConferenceService.getAppointedMeetings");
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {
				List<Appointment> appointments = appointmentLogic
						.getTodaysAppointmentsForUser(users_id);
				List<Rooms> result = new ArrayList<Rooms>();

				if (appointments != null) {
					for (int i = 0; i < appointments.size(); i++) {
						Appointment ment = appointments.get(i);

						Long rooms_id = ment.getRoom().getRooms_id();
						Rooms rooom = roomDao.get(rooms_id);

						rooom.setCurrentusers(this
								.getRoomClientsListByRoomId(rooom.getRooms_id()));
						result.add(rooom);
					}
				}

				log.debug("Found " + result.size() + " rooms");
				return result;
			}
		} catch (Exception err) {
			log.error("[getAppointedMeetingRoomsWithoutType]", err);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @return
	 */
	public List<RoomTypes> getRoomTypes(String SID) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			return roommanagement.getAllRoomTypes();
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomById(user_level, rooms_id);
	}

	public Rooms getRoomWithCurrentUsersById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		Rooms room = roommanagement.getRoomById(user_level, rooms_id);
		room.setCurrentusers(clientListManager.getClientListByRoom(room.getRooms_id()));
		return room;
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomByExternalId(String SID, Long externalUserId,
			String externalUserType, long roomtypes_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomByExternalId(user_level, externalUserId,
				externalUserType, roomtypes_id);
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public SearchResult<Rooms> getRooms(String SID, int start, int max,
			String orderby, boolean asc, String search) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRooms(user_level, start, max, orderby, asc,
				search);
	}

	public SearchResult<Rooms> getRoomsWithCurrentUsers(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsers(user_level, start, max,
				orderby, asc);
	}

	/**
	 * get all Organisations of a room
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public List<Rooms_Organisation> getOrganisationByRoom(String SID,
			long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getOrganisationsByRoom(user_level, rooms_id);
	}

	/**
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @deprecated
	 * @return
	 */
	// public Long addRoomPublic(String SID, String name, long roomtypes_id){
	// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	// Long User_level = userManagement.getUserLevelByID(users_id);
	// return roommanagement.addRoom(User_level, name,
	// roomtypes_id,"", new Long(4), true,null,
	// 290, 280, 2, 2,
	// 400,
	// true, 296, 2, 592, 660,
	// true, 2, 284, 310, 290, false, false, null);
	// }

	/**
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @deprecated
	 * @return
	 */
	// public Long addRoomOrganisation(String SID, long organisation_id, String
	// name, long roomtypes_id, boolean ispublic){
	// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	// long User_level = userManagement.getUserLevelByID(users_id);
	// Long rooms_id = roommanagement.addRoom(User_level, name,
	// roomtypes_id,"", new Long(4), ispublic, null,
	// 290, 280, 2, 2,
	// 400,
	// true, 296, 2, 592, 660,
	// true, 2, 284, 310, 290, false, false, null);
	// return roommanagement.addRoomToOrganisation(User_level,
	// rooms_id, organisation_id);
	// }

	/**
	 * 
	 * @param SID
	 * @param argObject
	 * @return
	 */
	public Long saveOrUpdateRoom(String SID, Object argObject) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			long User_level = userManagement.getUserLevelByID(users_id);
			log.debug("argObject: 1 - " + argObject.getClass().getName());
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> argObjectMap = (LinkedHashMap<String, Object>) argObject;
			log.debug("argObject: 2 - "
					+ argObjectMap.get("organisations").getClass().getName());
			@SuppressWarnings("unchecked")
			List<Integer> organisations = (List<Integer>) argObjectMap
					.get("organisations");
			Long rooms_id = Long.valueOf(
					argObjectMap.get("rooms_id").toString()).longValue();
			log.debug("rooms_id " + rooms_id);

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> roomModerators = (List<Map<String, Object>>) argObjectMap
					.get("roomModerators");

			Integer demoTime = null;
			if (argObjectMap.get("demoTime").toString() != null
					&& argObjectMap.get("demoTime").toString().length() > 0) {
				demoTime = Integer.valueOf(
						argObjectMap.get("demoTime").toString()).intValue();
			}

			long roomId = -1;
			if (rooms_id == 0) {
				roomId = roommanagement.addRoom(
						User_level,
						argObjectMap.get("name").toString(),
						Long.valueOf(
								argObjectMap.get("roomtypes_id").toString())
								.longValue(),
						argObjectMap.get("comment").toString(),
						Long.valueOf(
								argObjectMap.get("numberOfPartizipants")
										.toString()).longValue(), Boolean
								.valueOf(argObjectMap.get("ispublic")
										.toString()), organisations, Boolean
								.valueOf(argObjectMap.get("appointment")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isDemoRoom")
										.toString()), demoTime, Boolean
								.valueOf(argObjectMap.get("isModeratedRoom")
										.toString()), roomModerators, Boolean
								.valueOf(argObjectMap.get("allowUserQuestions")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isAudioOnly")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isClosed")
										.toString()),
						argObjectMap.get("redirectURL").toString(),
						argObjectMap.get("sipNumber").toString(), argObjectMap
								.get("conferencePin").toString(),
						Long.valueOf(argObjectMap.get("ownerId").toString())
								.longValue(), Boolean.valueOf(argObjectMap.get(
								"waitForRecording").toString()), Boolean
								.valueOf(argObjectMap.get("allowRecording")
										.toString()), Boolean
								.valueOf(argObjectMap.get("hideTopBar")
										.toString()), 
								Boolean.valueOf(argObjectMap.get("hideChat").toString()),
								Boolean.valueOf(argObjectMap.get("hideActivitiesAndActions").toString()),
								Boolean.valueOf(argObjectMap.get("hideFilesExplorer").toString()),
								Boolean.valueOf(argObjectMap.get("hideActionsMenu").toString()),
								Boolean.valueOf(argObjectMap.get("hideScreenSharing").toString()),
								Boolean.valueOf(argObjectMap.get("hideWhiteboard").toString()),
								Boolean.valueOf(argObjectMap.get("showMicrophoneStatus").toString()),
								Boolean.valueOf(argObjectMap.get("chatModerated").toString()),
								Boolean.valueOf(argObjectMap.get("chatOpened").toString()),
								Boolean.valueOf(argObjectMap.get("filesOpened").toString()),
								null
						);
			} else if (rooms_id > 0) {
				roomId = roommanagement.updateRoom(
								User_level,
								rooms_id,
								Long.valueOf(
										argObjectMap.get("roomtypes_id")
												.toString()).longValue(),
								argObjectMap.get("name").toString(),
								Boolean.valueOf(argObjectMap.get("ispublic")
										.toString()),
								argObjectMap.get("comment").toString(),
								Long.valueOf(
										argObjectMap
												.get("numberOfPartizipants")
												.toString()).longValue(),
								organisations,
								Boolean.valueOf(argObjectMap.get("appointment")
										.toString()),
								Boolean.valueOf(argObjectMap.get("isDemoRoom")
										.toString()),
								demoTime,
								Boolean.valueOf(argObjectMap.get(
										"isModeratedRoom").toString()),
								roomModerators,
								Boolean.valueOf(argObjectMap.get(
										"allowUserQuestions").toString()),
								Boolean.valueOf(argObjectMap.get("isAudioOnly")
										.toString()),
								Boolean.valueOf(argObjectMap.get("isClosed")
										.toString()),
								argObjectMap.get("redirectURL").toString(),
								argObjectMap.get("sipNumber").toString(),
								argObjectMap.get("conferencePin").toString(),
								Long.valueOf(
										argObjectMap.get("ownerId").toString())
										.longValue(),
								Boolean.valueOf(argObjectMap.get(
										"waitForRecording").toString()),
								Boolean.valueOf(argObjectMap.get(
										"allowRecording").toString()), 
								Boolean.valueOf(argObjectMap.get("hideTopBar").toString()),
								Boolean.valueOf(argObjectMap.get("hideChat").toString()),
								Boolean.valueOf(argObjectMap.get("hideActivitiesAndActions").toString()),
								Boolean.valueOf(argObjectMap.get("hideFilesExplorer").toString()),
								Boolean.valueOf(argObjectMap.get("hideActionsMenu").toString()),
								Boolean.valueOf(argObjectMap.get("hideScreenSharing").toString()),
								Boolean.valueOf(argObjectMap.get("hideWhiteboard").toString()),
								Boolean.valueOf(argObjectMap.get("showMicrophoneStatus").toString()),
								Boolean.valueOf(argObjectMap.get("chatModerated").toString()),
								Boolean.valueOf(argObjectMap.get("chatOpened").toString()),
								Boolean.valueOf(argObjectMap.get("filesOpened").toString())
								);
			}
			long serverId = -1;
			try {
				serverId = Long.parseLong(argObjectMap.get("serverId").toString());
			} catch (NumberFormatException nfe) {
				//noop expected;
			}
			if (roomId > -1 && serverId > -1) {
				try {
					Rooms room = roomDao.get(roomId);
					room.setServer(serverDao.get(serverId));
					roomDao.update(room, users_id);
				} catch (Exception e) {
					log.error("Error while setting server.");
				}
			}
			return roomId;

		} catch (Exception e) {
			log.error("saveOrUpdateRoom", e);
		}
		return null;
	}

	public List<RoomModerators> getRoomModeratorsByRoomId(String SID,
			Long roomId) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				return roomModeratorsDao.getRoomModeratorByRoomId(roomId);

			}

		} catch (Exception err) {
			log.error("[getRoomModeratorsByRoomId]", err);
			err.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Long deleteRoom(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.deleteRoomById(user_level, rooms_id);
	}
	
	/**
	 * return all participants of a room
	 * 
	 * @param room_id
	 * @return
	 */
	public boolean isRoomFull(Long room_id) {
		try {
			Rooms room = roomDao.get(room_id);
			
			if (room.getNumberOfPartizipants() <= this.clientListManager
					.getClientListByRoom(room_id).size()) {
				return true;
			}
			
			return false;
		} catch (Exception err) {
			log.error("[isRoomFull]", err);
		}
		return true;
	}

	/**
	 * return all participants of a room
	 * 
	 * @param room_id
	 * @return
	 */
	public List<RoomClient> getRoomClientsListByRoomId(Long room_id) {
		return clientListManager.getClientListByRoom(room_id);
	}

	public SearchResult<RoomClient> getRoomClientsMap(String SID, int start, int max,
			String orderby, boolean asc) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkAdminLevel(user_level)) {
				return this.clientListManager.getListByStartAndMax(start, max,
						orderby, asc);
			}
		} catch (Exception err) {
			log.error("[getRoomClientsMap]", err);
		}
		return null;
	}

	public List<Rooms> getRoomsWithCurrentUsersByList(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByList(user_level, start,
				max, orderby, asc);
	}

	public List<Rooms> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByListAndType(user_level,
				start, max, orderby, asc, externalRoomType);
	}

	public Rooms getRoomByOwnerAndType(String SID, Long roomtypesId,
			String roomName) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			return roommanagement.getRoomByOwnerAndTypeId(users_id,
					roomtypesId, roomName);
		}
		return null;
	}

}
