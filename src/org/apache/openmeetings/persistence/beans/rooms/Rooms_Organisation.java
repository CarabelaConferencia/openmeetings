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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "rooms_organisation")
@Root(name="room_organisation")
public class Rooms_Organisation implements Serializable {
	private static final long serialVersionUID = 4153935045968138984L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private Long rooms_organisation_id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="rooms_id", nullable=true)
	@Element(name="rooms_id", data=true, required=false)
	private Rooms room;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="organisation_id", nullable=true)
	@Element(name="organisation_id", data=true, required=false)
	private Organisation organisation;
	
	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	@Element(data=true)
	private boolean deleted;

	public Rooms_Organisation(Organisation org) {
		this.organisation = org;
	}

	public Rooms_Organisation() {
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public Rooms getRoom() {
		return room;
	}
	public void setRoom(Rooms room) {
		this.room = room;
	}

	public Long getRooms_organisation_id() {
		return rooms_organisation_id;
	}
	public void setRooms_organisation_id(Long rooms_organisation_id) {
		this.rooms_organisation_id = rooms_organisation_id;
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

}