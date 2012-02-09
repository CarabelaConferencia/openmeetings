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
package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.methodgateway.MethodGatewayResponse;
import org.openmeetings.app.persistence.beans.basic.RemoteSessionObject;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class MethodGateway extends HttpServlet {

	private static final long serialVersionUID = -2954875038645746731L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			MethodGateway.class, OpenmeetingsVariables.webAppRootKey);

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Usermanagement) context.getBean("userManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	public Roommanagement getRoommanagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Roommanagement) context.getBean("roommanagement");
			}
		} catch (Exception err) {
			log.error("[getRoommanagement]", err);
		}
		return null;
	}

	public MainService getMainService() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (MainService) context.getBean("xmlcrm.service");
			}
		} catch (Exception err) {
			log.error("[getMainService]", err);
		}
		return null;
	}

	public AuthLevelmanagement getAuthLevelManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (AuthLevelmanagement) context
						.getBean("authLevelManagement");
			}
		} catch (Exception err) {
			log.error("[getAuthLevelManagement]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {

			if (getUserManagement() == null || getSessionManagement() == null) {
				return;
			}

			String service = httpServletRequest.getParameter("service");

			if (service == null) {
				MethodGatewayResponse mGateway = new MethodGatewayResponse();
				mGateway.setMessage("No Service given");

				XStream xStream = new XStream(new XppDriver());
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(mGateway);

				OutputStream out = httpServletResponse.getOutputStream();

				out.write(xmlString.getBytes());
				out.close();
				return;
			}

			String method = httpServletRequest.getParameter("method");

			if (method == null) {
				MethodGatewayResponse mGateway = new MethodGatewayResponse();
				mGateway.setMessage("No Method given");

				XStream xStream = new XStream(new XppDriver());
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(mGateway);

				OutputStream out = httpServletResponse.getOutputStream();

				out.write(xmlString.getBytes());
				out.close();
				return;
			}

			log.debug("service: " + service + " method: " + method);

			// Services

			if (service.equals("userservice")) {

				if (method.equals("getSession")) {

					Sessiondata sessionData = getMainService().getsessiondata();

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(sessionData);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					out.close();
					return;
				} else if (method.equals("loginUser")) {

					String SID = httpServletRequest.getParameter("SID");
					String username = httpServletRequest
							.getParameter("username");
					String userpass = httpServletRequest
							.getParameter("userpass");

					Long returnVal = new Long(-1);

					Object obj = getUserManagement().loginUser(SID, username,
							userpass, null, false);
					if (obj == null) {
						returnVal = new Long(-1);
					}
					String objName = obj.getClass().getName();
					log.debug("objName: " + objName);
					if (objName.equals("java.lang.Long")) {
						returnVal = (Long) obj;
					} else {
						returnVal = new Long(1);
					}

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					out.close();
					return;
				} else if (method.equals("setUserObject")) {

					String SID = httpServletRequest.getParameter("SID");
					String username = httpServletRequest
							.getParameter("username");
					String firstname = httpServletRequest
							.getParameter("firstname");
					String lastname = httpServletRequest
							.getParameter("lastname");
					String profilePictureUrl = httpServletRequest
							.getParameter("profilePictureUrl");
					String email = httpServletRequest.getParameter("email");

					Long returnVal = new Long(-1);

					Long users_id = getSessionManagement().checkSession(SID);
					Long user_level = getUserManagement().getUserLevelByID(
							users_id);
					if (getAuthLevelManagement().checkAdminLevel(user_level)) {

						RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
								username, firstname, lastname,
								profilePictureUrl, email);

						XStream xStream = new XStream(new XppDriver());
						xStream.setMode(XStream.NO_REFERENCES);
						String xmlString = xStream.toXML(remoteSessionObject);

						getSessionManagement().updateUserRemoteSession(SID,
								xmlString);

						returnVal = new Long(1);
					} else {
						returnVal = new Long(-36);
					}

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					out.close();
					return;
				}

			} else if (service.equals("roomservice")) {
				if (method.equals("addRoom")) {

					String SID = httpServletRequest.getParameter("SID");
					String name = httpServletRequest.getParameter("name");
					Long roomtypes_id = Long.valueOf(
							httpServletRequest.getParameter("roomtypes_id"))
							.longValue();
					String comment = httpServletRequest.getParameter("comment");
					Long numberOfPartizipants = Long.valueOf(
							httpServletRequest
									.getParameter("numberOfPartizipants"))
							.longValue();
					Boolean ispublic = Boolean.valueOf(
							httpServletRequest.getParameter("ispublic"))
							.booleanValue();

					Long users_id = getSessionManagement().checkSession(SID);
					Long user_level = getUserManagement().getUserLevelByID(
							users_id);

					Long returnVal = getRoommanagement().addRoom(user_level,
							name, roomtypes_id, comment, numberOfPartizipants,
							ispublic, null, false, false, null, false, null,
							true, false, false, "", "", "", null, null, null, 
							false, // hideTopBar
							false, // hideChat
							false, // hideActivitiesAndActions
							false, // hideFilesExplorer
							false, // hideActionsMenu
							false, // hideScreenSharing 
							false, // hideWhiteboard
							false //showMicrophoneStatus
							);

					XStream xStream = new XStream(new XppDriver());
					xStream.setMode(XStream.NO_REFERENCES);
					String xmlString = xStream.toXML(returnVal);

					OutputStream out = httpServletResponse.getOutputStream();

					out.write(xmlString.getBytes());
					out.close();
					return;
				}

			}

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

}
