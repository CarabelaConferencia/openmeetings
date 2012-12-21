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
package org.apache.openmeetings.persistence.beans.rooms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.sip.asterisk.MeetMe;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getNondeletedRooms", query = "SELECT r FROM Rooms r WHERE r.deleted = false"),
	@NamedQuery(name = "getPublicRoomsOrdered", query = "SELECT r from Rooms r WHERE r.ispublic= true AND r.deleted= false AND r.appointment = false ORDER BY r.name ASC"),
	@NamedQuery(name = "getRoomById", query = "SELECT r FROM Rooms r WHERE r.deleted = false AND r.rooms_id = :id"),
	@NamedQuery(name = "countRooms", query = "SELECT COUNT(r) FROM Rooms r WHERE r.deleted = false"),
	@NamedQuery(name = "getBackupRooms", query = "SELECT r FROM Rooms r LEFT JOIN FETCH r.moderators WHERE r.deleted = false ")
})
@Table(name = "rooms")
@Root(name = "room")
public class Rooms implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = -2860312283159251568L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, required=false)
	private Long rooms_id;

	@Column(name = "name")
	@Element(data = true)
	private String name;

	@Lob
	@Column(name = "comment_field")
	@Element(data = true, required = false)
	private String comment;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomtypes_id")
	@Element(name = "roomtypeId", data = true, required = false)
	private RoomTypes roomtype;

	@Column(name = "starttime")
	private Date starttime;

	@Column(name = "updatetime")
	private Date updatetime;

	@Column(name = "deleted")
	@Element(data = true)
	private boolean deleted;

	@Column(name = "ispublic")
	@Element(data = true, required = false)
	private boolean ispublic;

	@Column(name = "numberOfPartizipants")
	@Element(data = true, required = false)
	private Long numberOfPartizipants = new Long(4);

	@Column(name = "appointment")
	@Element(data = true, required = false)
	private boolean appointment;

	// Vars to simulate external Rooms
	@Column(name = "externalRoomId")
	@Element(data = true, required = false)
	private Long externalRoomId;

	@Column(name = "externalRoomType")
	@Element(data = true, required = false)
	private String externalRoomType;

	@Column(name = "isdemoroom")
	@Element(data = true, required = false)
	private Boolean isDemoRoom;

	@Column(name = "demo_time")
	@Element(data = true, required = false)
	private Integer demoTime; // In Seconds

	// If this is true all participants of a meeting have to wait for the
	// moderator to come into the room
	@Column(name = "ismoderatedroom")
	@Element(data = true, required = false)
	private Boolean isModeratedRoom;

	@Column(name = "allow_user_questions")
	@Element(data = true, required = false)
	private Boolean allowUserQuestions;

	@Column(name = "is_audio_only")
	@Element(data = true, required = false)
	private Boolean isAudioOnly;
	
	@Column(name = "allow_font_styles")
	@Element(data = true)
	private Boolean allowFontStyles;

	@Column(name = "is_closed")
	@Element(data = true, required = false)
	private Boolean isClosed;

	@Column(name = "redirect_url")
	@Element(data = true, required = false)
	private String redirectURL;

	@Column(name = "owner_id")
	@Element(name = "ownerid", data = true, required = false)
	private Long ownerId; // Those are the rooms from the myrooms section

	@Column(name = "wait_for_recording")
	@Element(data = true, required = false)
	private Boolean waitForRecording; // Show warning that user has to start
										// recording

	@Column(name = "allow_recording")
	@Element(data = true, required = false)
	private Boolean allowRecording; // Show or show not the recording option in
									// a conference room
	/**
	 * Layout of Room
	 */
	@Column(name = "hide_top_bar")
	@Element(data = true, required = false)
	private Boolean hideTopBar = false;

	@Column(name = "hide_chat")
	@Element(data = true, required = false)
	private Boolean hideChat = false;

	@Column(name = "hide_activities_and_actions")
	@Element(data = true, required = false)
	private Boolean hideActivitiesAndActions = false;

	@Column(name = "hide_files_explorer")
	@Element(data = true, required = false)
	private Boolean hideFilesExplorer = false;

	@Column(name = "hide_actions_menu")
	@Element(data = true, required = false)
	private Boolean hideActionsMenu = false;

	@Column(name = "hide_screen_sharing")
	@Element(data = true, required = false)
	private Boolean hideScreenSharing = false;

	@Column(name = "hide_whiteboard")
	@Element(data = true, required = false)
	private Boolean hideWhiteboard = false;

	@Column(name = "show_microphone_status")
	@Element(data = true, required = false)
	private Boolean showMicrophoneStatus = false;

	@Column(name = "chat_moderated")
	@Element(data = true, required = false)
	private Boolean chatModerated = false;

	@Column(name = "chat_opened")
	@Element(data = true, required = false)
	private boolean chatOpened = false;

	@Column(name = "files_opened")
	@Element(data = true, required = false)
	private boolean filesOpened = false;

	@Column(name = "auto_video_select")
	@Element(data = true, required = false)
	private boolean autoVideoSelect = false;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "roomId")
	@ElementList(name = "room_moderators", required=false)
	private List<RoomModerators> moderators;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn(name="confno", referencedColumnName="confno")
	@Element(name = "meetme", required = false)
	private MeetMe meetme;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "rooms_id", insertable = true, updatable = true)
	@ElementDependent
	@org.simpleframework.xml.Transient
	private List<Rooms_Organisation> roomOrganisations = new ArrayList<Rooms_Organisation>();

	@Transient
	private List<RoomClient> currentusers;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getRooms_id() {
		return rooms_id;
	}

	public void setRooms_id(Long rooms_id) {
		this.rooms_id = rooms_id;
	}

	public RoomTypes getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(RoomTypes roomtype) {
		this.roomtype = roomtype;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getIspublic() {
		return ispublic;
	}

	public void setIspublic(Boolean ispublic) {
		this.ispublic = ispublic;
	}

	public List<RoomClient> getCurrentusers() {
		return currentusers;
	}

	public void setCurrentusers(List<RoomClient> currentusers) {
		this.currentusers = currentusers;
	}

	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}

	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}

	public Boolean getAppointment() {
		return appointment;
	}

	public void setAppointment(Boolean appointment) {
		this.appointment = appointment;
	}

	public Boolean getIsDemoRoom() {
		return isDemoRoom;
	}

	public void setIsDemoRoom(Boolean isDemoRoom) {
		this.isDemoRoom = isDemoRoom;
	}

	public Integer getDemoTime() {
		return demoTime;
	}

	public void setDemoTime(Integer demoTime) {
		this.demoTime = demoTime;
	}

	public Boolean getIsModeratedRoom() {
		return isModeratedRoom;
	}

	public void setIsModeratedRoom(Boolean isModeratedRoom) {
		this.isModeratedRoom = isModeratedRoom;
	}

	public Long getExternalRoomId() {
		return externalRoomId;
	}

	public void setExternalRoomId(Long externalRoomId) {
		this.externalRoomId = externalRoomId;
	}

	public String getExternalRoomType() {
		return externalRoomType;
	}

	public void setExternalRoomType(String externalRoomType) {
		this.externalRoomType = externalRoomType;
	}

	public Boolean getAllowUserQuestions() {
		return allowUserQuestions;
	}

	public void setAllowUserQuestions(Boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}

	public Boolean getIsAudioOnly() {
		return isAudioOnly;
	}

	public void setIsAudioOnly(Boolean isAudioOnly) {
		this.isAudioOnly = isAudioOnly;
	}

	public Boolean getAllowFontStyles() {
		return allowFontStyles;
	}

	public void setAllowFontStyles(Boolean allowFontStyles) {
		this.allowFontStyles = allowFontStyles;
	}

	public Boolean getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(Boolean isClosed) {
		this.isClosed = isClosed;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean getWaitForRecording() {
		return waitForRecording;
	}

	public void setWaitForRecording(Boolean waitForRecording) {
		this.waitForRecording = waitForRecording;
	}

	public Boolean getAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public Boolean getHideTopBar() {
		return hideTopBar;
	}

	public void setHideTopBar(Boolean hideTopBar) {
		this.hideTopBar = hideTopBar;
	}

	public Boolean getHideChat() {
		return hideChat;
	}

	public void setHideChat(Boolean hideChat) {
		this.hideChat = hideChat;
	}

	public Boolean getHideActivitiesAndActions() {
		return hideActivitiesAndActions;
	}

	public void setHideActivitiesAndActions(Boolean hideActivitiesAndActions) {
		this.hideActivitiesAndActions = hideActivitiesAndActions;
	}

	public Boolean getHideFilesExplorer() {
		return hideFilesExplorer;
	}

	public void setHideFilesExplorer(Boolean hideFilesExplorer) {
		this.hideFilesExplorer = hideFilesExplorer;
	}

	public Boolean getHideActionsMenu() {
		return hideActionsMenu;
	}

	public void setHideActionsMenu(Boolean hideActionsMenu) {
		this.hideActionsMenu = hideActionsMenu;
	}

	public Boolean getHideScreenSharing() {
		return hideScreenSharing;
	}

	public void setHideScreenSharing(Boolean hideScreenSharing) {
		this.hideScreenSharing = hideScreenSharing;
	}

	public Boolean getHideWhiteboard() {
		return hideWhiteboard;
	}

	public void setHideWhiteboard(Boolean hideWhiteboard) {
		this.hideWhiteboard = hideWhiteboard;
	}

	public Boolean getShowMicrophoneStatus() {
		return showMicrophoneStatus;
	}

	public void setShowMicrophoneStatus(Boolean showMicrophoneStatus) {
		this.showMicrophoneStatus = showMicrophoneStatus;
	}

	public List<RoomModerators> getModerators() {
		return moderators;
	}

	public void setModerators(List<RoomModerators> moderators) {
		this.moderators = moderators;
	}

	public Boolean getChatModerated() {
		return chatModerated;
	}

	public void setChatModerated(Boolean chatModerated) {
		this.chatModerated = chatModerated;
	}

	public List<Rooms_Organisation> getRoomOrganisations() {
		return roomOrganisations;
	}

	public void setRoomOrganisations(List<Rooms_Organisation> roomOrganisations) {
		this.roomOrganisations = roomOrganisations;
	}

	public boolean isChatOpened() {
		return chatOpened;
	}

	public void setChatOpened(boolean chatOpened) {
		this.chatOpened = chatOpened;
	}

	public boolean isFilesOpened() {
		return filesOpened;
	}

	public void setFilesOpened(boolean filesOpened) {
		this.filesOpened = filesOpened;
	}

	public boolean isAutoVideoSelect() {
		return autoVideoSelect;
	}

	public void setAutoVideoSelect(boolean autoVideoSelect) {
		this.autoVideoSelect = autoVideoSelect;
	}

	public MeetMe getMeetme() {
		return meetme;
	}

	public void setMeetme(MeetMe meetme) {
		this.meetme = meetme;
	}

}
