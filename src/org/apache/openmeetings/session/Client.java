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

import java.io.Serializable;
import java.util.Date;

import org.apache.openmeetings.utils.math.CalendarPatterns;

/**
 * @see IClientSession
 * @author sebawagner
 *
 */
public class Client implements Serializable, IClientSession {
	
	private static final long serialVersionUID = 1831858089607111565L;
	
	/**
	 * @see IClientSession#getUsername()
	 */
	private String username = "";
	/**
	 * @see IClientSession#getStreamid()
	 */
	private String streamid = "";
	/**
	 * @see IClientSession#getScope()
	 */
	private String scope = "";
	/**
	 * @see IClientSession#getVWidth()
	 */
	private int vWidth = 0;
	/**
	 * @see IClientSession#getVHeight()
	 */
	private int vHeight = 0;
	/**
	 * @see IClientSession#getVX()
	 */
	private int vX = 0;
	/**
	 * @see IClientSession#getVY()
	 */
	private int vY = 0;
	/**
	 * @see IClientSession#getStreamPublishName()
	 */
	private String streamPublishName = "";
	/**
	 * @see IClientSession#getPublicSID()
	 */
	private String publicSID = "";
	/**
	 * @see IClientSession#getIsMod()
	 */
	private Boolean isMod = false;
	/**
	 * @see IClientSession#getIsSuperModerator()
	 */
	private Boolean isSuperModerator = false;
	/**
	 * @see IClientSession#getCanDraw()
	 */
	private Boolean canDraw = false;
	/**
	 * @see IClientSession#getCanShare()
	 */
	private Boolean canShare = false;
	/**
	 * @see IClientSession#getCanRemote()
	 */
	private Boolean canRemote = false;
	/**
	 * @see IClientSession#getCanGiveAudio()
	 */
    private Boolean canGiveAudio = false;
    /**
	 * @see IClientSession#getConnectedSince()
	 */
	private Date connectedSince;
	/**
	 * @see IClientSession#getFormatedDate()
	 */
	private String formatedDate;
	/**
	 * @see IClientSession#getIsScreenClient()
	 */
	private Boolean isScreenClient = false;
	/**
	 * @see IClientSession#getIsAVClient()
	 */
	private boolean isAVClient = false;
	/**
	 * @see IClientSession#getUsercolor()
	 */
	private String usercolor;
	/**
	 * @see IClientSession#getUserpos()
	 */
	private Integer userpos;
	/**
	 * @see IClientSession#getUserip()
	 */
	private String userip;
	/**
	 * @see IClientSession#getUserport()
	 */
	private int userport;
	/**
	 * @see IClientSession#getRoom_id()
	 */
	private Long room_id;
	/**
	 * @see IClientSession#getRoomEnter()
	 */
	private Date roomEnter = null;
	/**
	 * @see IClientSession#getBroadCastID()
	 */
	private long broadCastID = -2;
	/**
	 * @see IClientSession#getUser_id()
	 */
	private Long user_id = null;
	/**
	 * @see IClientSession#getFirstname()
	 */
	private String firstname = "";
	/**
	 * @see IClientSession#getLastname()
	 */
	private String lastname = "";
	/**
	 * @see IClientSession#getMail()
	 */
	private String mail;
	/**
	 * @see IClientSession#getLastLogin()
	 */
	private String lastLogin;
	/**
	 * @see IClientSession#getOfficial_code()
	 */
	private String official_code;
	/**
	 * @see IClientSession#getPicture_uri()
	 */
	private String picture_uri;
	/**
	 * @see IClientSession#getLanguage()
	 */
	private String language = "";
	/**
	 * @see IClientSession#getAvsettings()
	 */
	private String avsettings = "";
	/**
	 * @see IClientSession#getSwfurl()
	 */
	// FIXME: Move to {@link ClientSession}
	private String swfurl;
	/**
	 * @see IClientSession#getIsRecording()
	 */
	private Boolean isRecording = false;
	/**
	 * @see IClientSession#getRoomRecordingName()
	 */
	private String roomRecordingName;
	/**
	 * @see IClientSession#getFlvRecordingId()
	 */
	private Long flvRecordingId;
	/**
	 * @see IClientSession#getFlvRecordingMetaDataId()
	 */
	private Long flvRecordingMetaDataId;
	/**
	 * @see IClientSession#getOrganization_id()
	 */
	private Long organization_id;
	/**
	 * @see IClientSession#isStartRecording()
	 */
	private boolean startRecording = false;
	/**
	 * @see IClientSession#isStartStreaming()
	 */
	private boolean startStreaming = false;
	/**
	 * @see IClientSession#isScreenPublishStarted()
	 */
	private boolean screenPublishStarted = false;
	/**
	 * @see IClientSession#isStreamPublishStarted()
	 */
	private boolean streamPublishStarted = false;
	/**
	 * @see IClientSession#getIsBroadcasting()
	 */
	private Boolean isBroadcasting = false;
	/**
	 * @see IClientSession#getExternalUserId()
	 */
	private String externalUserId;
	/**
	 * @see IClientSession#getExternalUserType()
	 */
    private String externalUserType;
    /**
	 * @see IClientSession#getInterviewPodId()
	 */
    private Integer interviewPodId = null;
    /**
	 * @see IClientSession#getAllowRecording()
	 */
    private Boolean allowRecording = true;
    /**
	 * @see IClientSession#getZombieCheckFlag()
	 */
	private Boolean zombieCheckFlag = false;
	/**
	 * @see IClientSession#getMicMuted()
	 */
    private Boolean micMuted = false;
    /**
	 * @see IClientSession#isSipTransport()
	 */
    private boolean sipTransport = false;
    
    public Client() {
    	
    }
    
	public Client(String streamid, String publicSID, Long room_id,
			Long user_id, String firstname, String lastname, boolean isAVClient,
			String username, String connectedSince, String scope) {
		super();
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.room_id = room_id;
		this.user_id = user_id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.isAVClient = isAVClient;
		this.username = username;
		this.connectedSince = CalendarPatterns.parseDateWithHour(connectedSince);
		this.scope = scope;
	}

	public void setUserObject(Long user_id, String username, String firstname, String lastname) {
		this.user_id = user_id;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public void setUserObject(String username, String firstname, String lastname) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}

	public Boolean getIsMod() {
		return isMod;
	}

	public void setIsMod(Boolean isMod) {
		this.isMod = isMod;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStreamid() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFormatedDate() {
		return formatedDate;
	}

	public void setFormatedDate(String formatedDate) {
		this.formatedDate = formatedDate;
	}

	public String getUsercolor() {
		return usercolor;
	}

	public void setUsercolor(String usercolor) {
		this.usercolor = usercolor;
	}

	public Integer getUserpos() {
		return userpos;
	}

	public void setUserpos(Integer userpos) {
		this.userpos = userpos;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public String getSwfurl() {
		return swfurl;
	}

	public void setSwfurl(String swfurl) {
		this.swfurl = swfurl;
	}

	public int getUserport() {
		return userport;
	}

	public void setUserport(int userport) {
		this.userport = userport;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getOfficial_code() {
		return official_code;
	}

	public void setOfficial_code(String official_code) {
		this.official_code = official_code;
	}

	public String getPicture_uri() {
		return picture_uri;
	}

	public void setPicture_uri(String picture_uri) {
		this.picture_uri = picture_uri;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Date getRoomEnter() {
		return roomEnter;
	}

	public void setRoomEnter(Date roomEnter) {
		this.roomEnter = roomEnter;
	}

	public Boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}

	public String getRoomRecordingName() {
		return roomRecordingName;
	}

	public void setRoomRecordingName(String roomRecordingName) {
		this.roomRecordingName = roomRecordingName;
	}

	public String getAvsettings() {
		return avsettings;
	}

	public void setAvsettings(String avsettings) {
		this.avsettings = avsettings;
	}

	public long getBroadCastID() {
		return broadCastID;
	}

	public void setBroadCastID(long broadCastID) {
		this.broadCastID = broadCastID;
	}

	public String getPublicSID() {
		return publicSID;
	}
	
	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	public Boolean getZombieCheckFlag() {
		return zombieCheckFlag;
	}
	
	public void setZombieCheckFlag(Boolean zombieCheckFlag) {
		this.zombieCheckFlag = zombieCheckFlag;
	}

	public Boolean getMicMuted() {
		return micMuted;
	}

	public void setMicMuted(Boolean micMuted) {
		this.micMuted = micMuted;
	}

	public Boolean getCanDraw() {
		return canDraw;
	}

	public void setCanDraw(Boolean canDraw) {
		this.canDraw = canDraw;
	}

	public Boolean getIsBroadcasting() {
		return isBroadcasting;
	}

	public void setIsBroadcasting(Boolean isBroadcasting) {
		this.isBroadcasting = isBroadcasting;
	}

	public Boolean getCanShare() {
		return canShare;
	}

	public void setCanShare(Boolean canShare) {
		this.canShare = canShare;
	}

	public String getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getExternalUserType() {
		return externalUserType;
	}

	public void setExternalUserType(String externalUserType) {
		this.externalUserType = externalUserType;
	}

	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}

	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}

	public Boolean getIsScreenClient() {
		return isScreenClient;
	}

	public void setIsScreenClient(Boolean isScreenClient) {
		this.isScreenClient = isScreenClient;
	}

	public int getVWidth() {
		return vWidth;
	}

	public void setVWidth(int width) {
		vWidth = width;
	}

	public int getVHeight() {
		return vHeight;
	}

	public void setVHeight(int height) {
		vHeight = height;
	}

	public int getVX() {
		return vX;
	}

	public void setVX(int vx) {
		vX = vx;
	}

	public int getVY() {
		return vY;
	}

	public void setVY(int vy) {
		vY = vy;
	}

	public String getStreamPublishName() {
		return streamPublishName;
	}

	public void setStreamPublishName(String streamPublishName) {
		this.streamPublishName = streamPublishName;
	}

	public Long getFlvRecordingId() {
		return flvRecordingId;
	}

	public void setFlvRecordingId(Long flvRecordingId) {
		this.flvRecordingId = flvRecordingId;
	}

	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}

	public void setFlvRecordingMetaDataId(Long flvRecordingMetaDataId) {
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
	}

	public boolean isScreenPublishStarted() {
		return screenPublishStarted;
	}

	public void setScreenPublishStarted(boolean screenPublishStarted) {
		this.screenPublishStarted = screenPublishStarted;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public boolean isStartRecording() {
		return startRecording;
	}

	public void setStartRecording(boolean startRecording) {
		this.startRecording = startRecording;
	}

	public boolean isStartStreaming() {
		return startStreaming;
	}

	public void setStartStreaming(boolean startStreaming) {
		this.startStreaming = startStreaming;
	}

	public Integer getInterviewPodId() {
		return interviewPodId;
	}

	public void setInterviewPodId(Integer interviewPodId) {
		this.interviewPodId = interviewPodId;
	}

	public Boolean getCanRemote() {
		return canRemote;
	}

	public void setCanRemote(Boolean canRemote) {
		this.canRemote = canRemote;
	}

    public Boolean getCanGiveAudio() {
		return canGiveAudio;
	}

	public void setCanGiveAudio(Boolean canGiveAudio) {
		this.canGiveAudio = canGiveAudio;
	}

	public Boolean getAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public boolean getIsAVClient() {
		return isAVClient;
	}

	public void setIsAVClient(boolean isAVClient) {
		this.isAVClient = isAVClient;
	}

	public boolean isStreamPublishStarted() {
		return streamPublishStarted;
	}

	public void setStreamPublishStarted(boolean streamPublishStarted) {
		this.streamPublishStarted = streamPublishStarted;
	}

	public boolean isSipTransport() {
		return sipTransport;
	}

	public void setSipTransport(boolean sipTransport) {
		this.sipTransport = sipTransport;
	}
	
	@Override
	public String toString() {
		return super.toString() //
				+ " StreamId: " + this.getStreamid() //
				+ " PublicSID: " + this.getPublicSID() //
				+ " UserId: " + this.getUser_id() //
				+ " isScreenClient: " + this.getIsScreenClient() //
				+ " flvRecordingId: " + this.getFlvRecordingId() //
				+ " screenPublishStarted: " + this.isScreenPublishStarted() //
				+ " flvRecordingMetaDataId: " + this.getFlvRecordingMetaDataId() //
				+ " isRecording: " + this.getIsRecording() //
				+ " isAVClient: " + this.getIsAVClient() //
				+ " broadCastID: " + this.getBroadCastID() //
				+ " avsettings: " + this.getAvsettings() //
				;
	}
	
}
