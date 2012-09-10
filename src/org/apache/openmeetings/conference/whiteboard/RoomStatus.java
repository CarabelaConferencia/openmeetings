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
package org.apache.openmeetings.conference.whiteboard;

import java.util.List;

import org.apache.openmeetings.persistence.beans.rooms.RoomClient;

public class RoomStatus {
	
	List<RoomClient> clientList;
	BrowserStatus browserStatus;
	Boolean roomFull = false;
	
	public List<RoomClient> getClientList() {
		return clientList;
	}
	public void setClientList(List<RoomClient> clientList) {
		this.clientList = clientList;
	}
	public BrowserStatus getBrowserStatus() {
		return browserStatus;
	}
	public void setBrowserStatus(BrowserStatus browserStatus) {
		this.browserStatus = browserStatus;
	}
	public Boolean getRoomFull() {
		return roomFull;
	}
	public void setRoomFull(Boolean roomFull) {
		this.roomFull = roomFull;
	}
	
}
