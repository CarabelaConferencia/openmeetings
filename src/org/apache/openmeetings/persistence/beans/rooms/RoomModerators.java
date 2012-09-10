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
import java.util.Date;


import org.apache.openmeetings.persistence.beans.user.Users;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "rooms_moderator")
@Root(name = "room_moderator")
public class RoomModerators implements Serializable {
	private static final long serialVersionUID = 5407758673591515018L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long roomModeratorsId;
	
	@Column(name = "roomId")
	private Long roomId;
	
	@Column(name="is_supermoderator")
	@Element(name="is_supermoderator", data = true)
	private Boolean isSuperModerator;
	
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn (name="user_id")
	@Element(name="user_id", data = true, required=false)
	private Users user;
	
	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	private boolean deleted;
	
	public long getRoomModeratorsId() {
		return roomModeratorsId;
	}
	public void setRoomModeratorsId(long roomModeratorsId) {
		this.roomModeratorsId = roomModeratorsId;
	}
	
	public Boolean getIsSuperModerator() {
		return isSuperModerator;
	}
	public void setIsSuperModerator(Boolean isSuperModerator) {
		this.isSuperModerator = isSuperModerator;
	}
	
	
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
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
	
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	
}
