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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.backup.AppointmentCategoryConverter;
import org.apache.openmeetings.backup.AppointmentConverter;
import org.apache.openmeetings.backup.AppointmentReminderTypeConverter;
import org.apache.openmeetings.backup.DateConverter;
import org.apache.openmeetings.backup.IntegerTransform;
import org.apache.openmeetings.backup.LongTransform;
import org.apache.openmeetings.backup.OmTimeZoneConverter;
import org.apache.openmeetings.backup.OrganisationConverter;
import org.apache.openmeetings.backup.PollTypeConverter;
import org.apache.openmeetings.backup.RoomConverter;
import org.apache.openmeetings.backup.RoomTypeConverter;
import org.apache.openmeetings.backup.StateConverter;
import org.apache.openmeetings.backup.UserConverter;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.LdapConfigDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentCategoryDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.PollManagement;
import org.apache.openmeetings.data.conference.RoomDAO;
import org.apache.openmeetings.data.conference.Roommanagement;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.user.Organisationmanagement;
import org.apache.openmeetings.data.user.dao.OrganisationDao;
import org.apache.openmeetings.data.user.dao.PrivateMessageFolderDao;
import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.data.user.dao.UserContactsDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.documents.beans.UploadCompleteMessage;
import org.apache.openmeetings.persistence.beans.adresses.Adresses;
import org.apache.openmeetings.persistence.beans.adresses.States;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentCategory;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.persistence.beans.poll.PollType;
import org.apache.openmeetings.persistence.beans.poll.RoomPoll;
import org.apache.openmeetings.persistence.beans.rooms.RoomTypes;
import org.apache.openmeetings.persistence.beans.rooms.Rooms;
import org.apache.openmeetings.persistence.beans.rooms.Rooms_Organisation;
import org.apache.openmeetings.persistence.beans.sip.asterisk.AsteriskSipUsers;
import org.apache.openmeetings.persistence.beans.sip.asterisk.Extensions;
import org.apache.openmeetings.persistence.beans.sip.asterisk.MeetMe;
import org.apache.openmeetings.persistence.beans.user.PrivateMessageFolder;
import org.apache.openmeetings.persistence.beans.user.PrivateMessages;
import org.apache.openmeetings.persistence.beans.user.UserContacts;
import org.apache.openmeetings.persistence.beans.user.UserSipData;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.sip.api.impl.asterisk.dao.AsteriskDAOImpl;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeBuilder;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Controller
public class BackupImportController extends AbstractUploadController {

	private static final Logger log = Red5LoggerFactory.getLogger(
			BackupImportController.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private RoomDAO roomDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private FlvRecordingDao flvRecordingDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private PrivateMessagesDao privateMessagesDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;
	@Autowired
	private UserContactsDao userContactsDao;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private PollManagement pollManagement;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private AsteriskDAOImpl asteriskDAOImpl;

	private final HashMap<Long, Long> usersMap = new HashMap<Long, Long>();
	private final HashMap<Long, Long> organisationsMap = new HashMap<Long, Long>();
	private final HashMap<Long, Long> appointmentsMap = new HashMap<Long, Long>();
	private final HashMap<Long, Long> roomsMap = new HashMap<Long, Long>();
	private final HashMap<Long, Long> messageFoldersMap = new HashMap<Long, Long>();
	private final HashMap<Long, Long> userContactsMap = new HashMap<Long, Long>();

	private enum Maps {
		USERS, ORGANISATIONS, APPOINTMENTS, ROOMS, MESSAGEFOLDERS, USERCONTACTS
	};

	public void performImport(InputStream is) throws Exception {
		File working_dir = OmFileHelper.getUploadImportDir();
		if (!working_dir.exists()) {
			working_dir.mkdir();
		}

		File f = OmFileHelper.getNewDir(working_dir, "import_" + CalendarPatterns.getTimeForStreamId(new Date()));

		log.debug("##### WRITE FILE TO: " + f);
		
		ZipInputStream zipinputstream = new ZipInputStream(is);

		ZipEntry zipentry = zipinputstream.getNextEntry();

		while (zipentry != null) {
			// for each entry to be extracted
			File fentryName = new File(f, zipentry.getName());

			if (zipentry.isDirectory()) {
				if (!fentryName.mkdir()) {
					break;
				}
				zipentry = zipinputstream.getNextEntry();
				continue;
			}

			File fparent = new File(fentryName.getParent());

			if (!fparent.exists()) {

				File fparentparent = new File(fparent.getParent());

				if (!fparentparent.exists()) {

					File fparentparentparent = new File(
							fparentparent.getParent());

					if (!fparentparentparent.exists()) {

						fparentparentparent.mkdir();
						fparentparent.mkdir();
						fparent.mkdir();

					} else {

						fparentparent.mkdir();
						fparent.mkdir();

					}

				} else {

					fparent.mkdir();

				}

			}

			FileHelper.copy(zipinputstream, fentryName);
			zipinputstream.closeEntry();
			zipentry = zipinputstream.getNextEntry();

		}// while

		zipinputstream.close();

		/*
		 * ##################### Import Organizations
		 */
		Serializer simpleSerializer = new Persister();
		{
			List<Organisation> list = readList(simpleSerializer, f, "organizations.xml", "organisations", Organisation.class);
			for (Organisation o : list) {
				long oldId = o.getOrganisation_id();
				o.setOrganisation_id(null);
				Long newId = organisationmanagement.addOrganisationObj(o);
				organisationsMap.put(oldId, newId);
			}
		}

		log.info("Organizations import complete, starting user import");
		/*
		 * ##################### Import Users
		 */
		{
			List<Users> list = readUserList(f, "users.xml", "users");
			for (Users u : list) {
				OmTimeZone tz = u.getOmTimeZone();
				if (tz == null || tz.getJname() == null) {
					String jNameTimeZone = configurationDao.getConfValue(
							"default.timezone", String.class, "Europe/Berlin");
					OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jNameTimeZone);
					u.setOmTimeZone(omTimeZone);
					u.setForceTimeZoneCheck(true);
				} else {
					u.setForceTimeZoneCheck(false);
				}
				
				u.setStarttime(new Date());
				long userId = u.getUser_id();
				u.setUser_id(null);
				Long actualNewUserId = userManagement.addUserBackup(u);
				usersMap.put(userId, actualNewUserId);
			}
		}

		log.info("Users import complete, starting room import");
		/*
		 * ##################### Import Rooms
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			registry.bind(RoomTypes.class, new RoomTypeConverter(roommanagement));
			
			List<Rooms> list = readList(serializer, f, "rooms.xml", "rooms", Rooms.class);
			for (Rooms r : list) {
				Long roomId = r.getRooms_id();

				// We need to reset this as openJPA reject to store them
				// otherwise
				r.setRooms_id(null);

				r = roomDao.update(r, 1L);
				roomsMap.put(roomId, r.getRooms_id());
			}
		}

		log.info("Room import complete, starting room organizations import");
		/*
		 * ##################### Import Room Organisations
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Organisation.class, new OrganisationConverter(orgDao, organisationsMap));
			registry.bind(Rooms.class, new RoomConverter(roomDao, roomsMap));
			
			List<Rooms_Organisation> list = readList(serializer, f, "rooms_organisation.xml", "room_organisations", Rooms_Organisation.class);
			for (Rooms_Organisation ro : list) {
				if (!ro.getDeleted()) {
					// We need to reset this as openJPA reject to store them otherwise
					ro.setRooms_organisation_id(null);
					roommanagement.addRoomOrganisation(ro);
				}
			}
		}

		log.info("Room organizations import complete, starting appointement import");
		/*
		 * ##################### Import Appointements
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(AppointmentCategory.class, new AppointmentCategoryConverter(appointmentCategoryDaoImpl));
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			registry.bind(AppointmentReminderTyps.class, new AppointmentReminderTypeConverter(appointmentReminderTypDaoImpl));
			registry.bind(Rooms.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(Date.class, DateConverter.class);
			
			List<Appointment> list = readList(serializer, f, "appointements.xml", "appointments", Appointment.class);
			for (Appointment a : list) {
				Long appId = a.getAppointmentId();

				// We need to reset this as openJPA reject to store them otherwise
				a.setAppointmentId(null);

				Long newAppId = appointmentDao.addAppointmentObj(a);
				appointmentsMap.put(appId, newAppId);
			}
		}

		log.info("Appointement import complete, starting meeting members import");
		/*
		 * ##################### Import MeetingMembers
		 * 
		 * Reminder Invitations will be NOT send!
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			registry.bind(Appointment.class, new AppointmentConverter(appointmentDao, appointmentsMap));
			
			List<MeetingMember> list = readList(serializer, f, "meetingmembers.xml", "meetingmembers", MeetingMember.class);
			for (MeetingMember ma : list) {
				if (ma.getUserid() != null && ma.getUserid().getUser_id() == null) {
					ma.setUserid(null);
				}
				if (!ma.getDeleted()) {
					// We need to reset this as openJPA reject to store them otherwise
					ma.setMeetingMemberId(null);
					meetingMemberDao.addMeetingMemberByObject(ma);
				}
			}
		}

		log.info("Meeting members import complete, starting ldap config import");
		/*
		 * ##################### Import LDAP Configs
		 */
		{
			List<LdapConfig> list = readList(simpleSerializer, f, "ldapconfigs.xml", "ldapconfigs", LdapConfig.class, true);
			for (LdapConfig c : list) {
				ldapConfigDao.addLdapConfigByObject(c);
			}
		}

		log.info("Ldap config import complete, starting recordings import");
		/*
		 * ##################### Import Recordings
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);
			
			List<FlvRecording> list = readList(serializer, f, "flvRecordings.xml", "flvrecordings", FlvRecording.class, true);
			for (FlvRecording fr : list) {
				fr.setFlvRecordingId(0);
				if (fr.getRoom_id() != null) {
					fr.setRoom_id(roomsMap.get(fr.getRoom_id()));
				}
				if (fr.getOwnerId() != null) {
					fr.setOwnerId(usersMap.get(fr.getOwnerId()));
				}
				if (fr.getFlvRecordingMetaData() != null) {
					for (FlvRecordingMetaData meta : fr.getFlvRecordingMetaData()) {
						meta.setFlvRecordingMetaDataId(0);
						meta.setFlvRecording(fr);
					}
				}
				flvRecordingDao.addFlvRecordingObj(fr);
			}
		}

		log.info("FLVrecording import complete, starting private message folder import");
		/*
		 * ##################### Import Private Message Folders
		 */
		{
			List<PrivateMessageFolder> list = readList(simpleSerializer, f, "privateMessageFolder.xml"
				, "privatemessagefolders", PrivateMessageFolder.class, true);
			for (PrivateMessageFolder p : list) {
				Long folderId = p.getPrivateMessageFolderId();
				PrivateMessageFolder storedFolder = privateMessageFolderDao
						.getPrivateMessageFolderById(folderId);
				if (storedFolder == null) {
					p.setPrivateMessageFolderId(0);
					Long newFolderId = privateMessageFolderDao
							.addPrivateMessageFolderObj(p);
					messageFoldersMap.put(folderId, newFolderId);
				}
			}
		}

		log.info("Private message folder import complete, starting user contacts import");
		/*
		 * ##################### Import User Contacts
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			
			List<UserContacts> list = readList(serializer, f, "userContacts.xml", "usercontacts", UserContacts.class, true);
			for (UserContacts uc : list) {
				Long ucId = uc.getUserContactId();
				UserContacts storedUC = userContactsDao.getUserContacts(ucId);

				if (storedUC == null && uc.getContact() != null && uc.getContact().getUser_id() != null) {
					uc.setUserContactId(0);
					Long newId = userContactsDao.addUserContactObj(uc);
					userContactsMap.put(ucId, newId);
				}
			}
		}

		log.info("Usercontact import complete, starting private messages item import");
		/*
		 * ##################### Import Private Messages
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			registry.bind(Rooms.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(Date.class, DateConverter.class);
			
			List<PrivateMessages> list = readList(serializer, f, "privateMessages.xml", "privatemessages", PrivateMessages.class, true);
			for (PrivateMessages p : list) {
				p.setPrivateMessageId(0);
				p.setPrivateMessageFolderId(
					getNewId(p.getPrivateMessageFolderId(), Maps.MESSAGEFOLDERS));
				p.setUserContactId(
					getNewId(p.getUserContactId(), Maps.USERCONTACTS));
				if (p.getRoom() != null && p.getRoom().getRooms_id() == null) {
					p.setRoom(null);
				}
				if (p.getTo() != null && p.getTo().getUser_id() == null) {
					p.setTo(null);
				}
				privateMessagesDao.addPrivateMessageObj(p);
			}
		}

		log.info("Private message import complete, starting file explorer item import");
		/*
		 * ##################### Import File-Explorer Items
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			matcher.bind(Integer.class, IntegerTransform.class);
			registry.bind(Date.class, DateConverter.class);
			
			List<FileExplorerItem> list = readList(serializer, f, "fileExplorerItems.xml", "fileExplorerItems", FileExplorerItem.class, true);
			for (FileExplorerItem fileExplorerItem : list) {
				// We need to reset this as openJPA reject to store them otherwise
				fileExplorerItem.setFileExplorerItemId(0);
				fileExplorerItemDao.addFileExplorerItem(fileExplorerItem);
			}
		}

		log.info("File explorer item import complete, starting file poll import");
		/*
		 * ##################### Import Room Polls
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			Serializer serializer = new Persister(strategy);
	
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			registry.bind(Rooms.class, new RoomConverter(roomDao, roomsMap));
			registry.bind(PollType.class, new PollTypeConverter(pollManagement));
			registry.bind(Date.class, DateConverter.class);
			
			List<RoomPoll> list = readList(serializer, f, "roompolls.xml", "roompolls", RoomPoll.class, true);
			for (RoomPoll rp : list) {
				pollManagement.savePollBackup(rp);
			}
		}
		
		log.info("Poll import complete, starting configs import");
		/*
		 * ##################### Import Configs
		 */
		{
			Registry registry = new Registry();
			Strategy strategy = new RegistryStrategy(registry);
			RegistryMatcher matcher = new RegistryMatcher(); //TODO need to be removed in the later versions
			Serializer serializer = new Persister(strategy, matcher);

			matcher.bind(Long.class, LongTransform.class);
			registry.bind(Date.class, DateConverter.class);
			registry.bind(Users.class, new UserConverter(usersDao, usersMap));
			
			List<Configuration> list = readList(serializer, f, "configs.xml", "configs", Configuration.class, true);
			for (Configuration c : list) {
				Configuration cfg = configurationDao.getConfKey(c
						.getConf_key());
				c.setConfiguration_id(cfg == null ? null : cfg.getConfiguration_id());
				if (c.getUser() != null && c.getUser().getUser_id() == null) {
					c.setUser(null);
				}
				configurationDao.update(c, 1L);
			}
		}

		log.info("Configs import complete, starting asteriskSipUsersFile import");
		/*
		 * ##################### Import AsteriskSipUsers
		 */
		{
			List<AsteriskSipUsers> list = readList(simpleSerializer, f, "asterisksipusers.xml"
				, "asterisksipusers", AsteriskSipUsers.class, true);
			for (AsteriskSipUsers au : list) {
				au.setId(0);
				asteriskDAOImpl.saveAsteriskSipUsers(au);
			}
		}

		log.info("AsteriskSipUsers import complete, starting extensions import");
		/*
		 * ##################### Import Extensions
		 */
		{
			List<Extensions> list = readList(simpleSerializer, f, "extensions.xml"
				, "extensions", Extensions.class, true);
			for (Extensions e : list) {
				e.setId(null);
				asteriskDAOImpl.saveExtensions(e);
			}
		}

		log.info("Extensions import complete, starting MeetMe members import");
		/*
		 * ##################### Import MeetMe
		 */
		{
			List<MeetMe> list = readList(simpleSerializer, f, "members.xml"
				, "members", MeetMe.class, true);
			for (MeetMe mm : list) {
				asteriskDAOImpl.saveMeetMe(mm);
			}
		}

		log.info("Members import complete, starting copy of files and folders");
		/*
		 * ##################### Import real files and folders
		 */
		importFolders(f);

		log.info("File explorer item import complete, clearing temp files");
		
		FileHelper.removeRec(f);
	}
	
	@RequestMapping(value = "/backup.upload", method = RequestMethod.POST)
	public void service(HttpServletRequest request,
			HttpServletResponse httpServletResponse)
			throws ServletException, IOException {

    	UploadInfo info = validate(request, true);
    	try {
			MultipartFile multipartFile = info.file;
			InputStream is = multipartFile.getInputStream();
			performImport(is);

			UploadCompleteMessage uploadCompleteMessage = new UploadCompleteMessage(
						info.userId,
						"library", //message
						"import", //action
						"", //error
						info.filename);
			
			scopeApplicationAdapter.sendUploadCompletMessageByPublicSID(
					uploadCompleteMessage, info.publicSID);

		} catch (Exception e) {

			log.error("[ImportExport]", e);

			e.printStackTrace();
			throw new ServletException(e);
		}

		return;
	}

	private <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz) throws Exception {
		return readList(ser, baseDir, fileName, listNodeName, clazz, false);
	}
	
	private <T> List<T> readList(Serializer ser, File baseDir, String fileName, String listNodeName, Class<T> clazz, boolean notThow) throws Exception {
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			final String msg = fileName + " missing";
			if (notThow) {
				log.debug(msg);
			} else {
				throw new Exception(msg);
			}
		}
		List<T> list = new ArrayList<T>();
		InputNode root = NodeBuilder.read(new FileInputStream(xml));
		InputNode listNode = root.getNext();
		if (listNodeName.equals(listNode.getName())) {
			InputNode item = listNode.getNext();
			while (item != null) {
				try {
					T o = ser.read(clazz, item, false);
					list.add(o);
				} catch (Exception e) {
					log.debug("Exception While reading node of type: " + clazz, e);
				}
				item = listNode.getNext();
			}
		}
		return list;
	}
	
	private Node getNode(Node doc, String name) {
		NodeList nl = doc.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && name.equals(node.getNodeName())) {
				return node;
			}
		}
		return null;
	}
	
	public List<Users> readUserList(InputStream xml, String listNodeName) throws Exception {
		return readUserList(new InputSource(xml), listNodeName);
	}
	
	public List<Users> readUserList(File baseDir, String fileName, String listNodeName) throws Exception {
		File xml = new File(baseDir, fileName);
		if (!xml.exists()) {
			throw new Exception(fileName + " missing");
		}
		
		return readUserList(new InputSource(xml.toURI().toASCIIString()), listNodeName);
	}
	
	//FIXME (need to be removed in later versions) HACK to fix 2 deleted nodes in users.xml and inline Adresses and sipData
	private List<Users> readUserList(InputSource xml, String listNodeName) throws Exception {
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Serializer ser = new Persister(strategy);

		registry.bind(Organisation.class, new OrganisationConverter(orgDao, organisationsMap));
		//registry.bind(UserSipData.class, UserSipDataConverter.class);
		registry.bind(OmTimeZone.class, new OmTimeZoneConverter(omTimeZoneDaoImpl));
		registry.bind(States.class, new StateConverter(statemanagement));
		registry.bind(Date.class, DateConverter.class);

		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(xml);
		NodeList nl = getNode(getNode(doc, "root"), listNodeName).getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node user = nl.item(i);
			NodeList nl1 = user.getChildNodes();
			boolean deletedFound = false;
			for (int j = 0; j < nl1.getLength(); ++j) {
				Node node = nl1.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE && "deleted".equals(node.getNodeName())) {
					if (deletedFound) {
						user.removeChild(node);
						break;
					}
					deletedFound = true;
				}
			}
		}
		
		StringWriter sw = new StringWriter();
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(new DOMSource(doc), new StreamResult(sw));
        
		List<Users> list = new ArrayList<Users>();
		InputNode root = NodeBuilder.read(new StringReader(sw.toString()));
		InputNode root1 = NodeBuilder.read(new StringReader(sw.toString())); //HACK to handle Adresses inside user
		InputNode root2 = NodeBuilder.read(new StringReader(sw.toString())); //HACK to handle UserSipData inside user
		InputNode listNode = root.getNext();
		InputNode listNode1 = root1.getNext(); //HACK to handle Adresses inside user
		InputNode listNode2 = root2.getNext(); //HACK to handle UserSipData inside user
		if (listNodeName.equals(listNode.getName())) {
			InputNode item = listNode.getNext();
			InputNode item1 = listNode1.getNext(); //HACK to handle Adresses inside user
			InputNode item2 = listNode2.getNext(); //HACK to handle UserSipData inside user
			while (item != null) {
				try {
					Users u = ser.read(Users.class, item, false);
					
					//HACK to handle Adresses and UserSipData inside user
					if (u.getAdresses() == null) {
						Adresses a = ser.read(Adresses.class, item1, false);
						u.setAdresses(a);
					}
					if (u.getUserSipData() == null) {
						UserSipData usd = ser.read(UserSipData.class, item2, false);
						u.setUserSipData(usd);
					}
					list.add(u);
				} catch (Exception e) {
					log.debug("Exception While reading node of type: " + Users.class, e);
				}
				item = listNode.getNext();
				do {
					item1 = listNode1.getNext(); //HACK to handle Adresses inside user
				} while (item != null && !"user".equals(item1.getName()));
				do {
					item2 = listNode2.getNext(); //HACK to handle UserSipData inside user
				} while (item != null && !"user".equals(item2.getName()));
			}
		}
		return list;
	}
	
	private void importFolders(File importBaseDir)
			throws IOException {

		// Now check the room files and import them
		File roomFilesFolder = new File(importBaseDir, "roomFiles");

		File library_dir = OmFileHelper.getUploadDir();

		log.debug("roomFilesFolder PATH " + roomFilesFolder.getCanonicalPath());

		if (roomFilesFolder.exists()) {

			File[] files = roomFilesFolder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {

					File parentPathFile = new File(library_dir, file.getName());

					if (!parentPathFile.exists()) {
						parentPathFile.mkdir();
					}

					File[] roomOrProfileFiles = file.listFiles();
					for (File roomOrProfileFileOrFolder : roomOrProfileFiles) {

						if (roomOrProfileFileOrFolder.isDirectory()) {

							String fileOrFolderName = roomOrProfileFileOrFolder
									.getName();
							int beginIndex = fileOrFolderName
									.indexOf(OmFileHelper.profilesPrefix);
							// Profile folder should be renamed if new user id
							// is differ from current id.
							if (beginIndex > -1) {
								beginIndex = beginIndex
										+ OmFileHelper.profilesPrefix
												.length();
								Long profileId = importLongType(fileOrFolderName
										.substring(beginIndex));
								Long newProfileID = getNewId(profileId,
										Maps.USERS);
								if (profileId != newProfileID) {
									fileOrFolderName = fileOrFolderName
											.replaceFirst(
													OmFileHelper.profilesPrefix
															+ profileId,
													OmFileHelper.profilesPrefix
															+ newProfileID);
								}
							}
							File roomDocumentFolder = new File(parentPathFile, fileOrFolderName);

							if (!roomDocumentFolder.exists()) {
								roomDocumentFolder.mkdir();

								File[] roomDocumentFiles = roomOrProfileFileOrFolder
										.listFiles();

								for (File roomDocumentFile : roomDocumentFiles) {
									if (roomDocumentFile.isDirectory()) {
										log.error("Folder detected in Documents space! Folder " + roomDocumentFolder);
									} else {
										FileHelper.copy(roomDocumentFile, new File(roomDocumentFolder, roomDocumentFile.getName()));
									}
								}
							} else {
								log.debug("Document already exists :: ",
										roomDocumentFolder);
							}
						} else {
							File roomFileOrProfileFile = new File(parentPathFile, roomOrProfileFileOrFolder.getName());
							if (!roomFileOrProfileFile.exists()) {
								FileHelper.copy(roomOrProfileFileOrFolder, roomFileOrProfileFile);
							} else {
								log.debug("File does already exist :: ", roomFileOrProfileFile);
							}
						}
					}
				}
			}
		}

		// Now check the recordings and import them

		File sourceDirRec = new File(importBaseDir, "recordingFiles");

		log.debug("sourceDirRec PATH " + sourceDirRec.getCanonicalPath());

		if (sourceDirRec.exists()) {
			File targetDirRec = OmFileHelper.getStreamsHibernateDir();

			FileHelper.copyRec(sourceDirRec, targetDirRec);
		}
	}

	private Long importLongType(String value) {

		if (value.equals("null") || value.equals("")) {
			return null;
		}

		return Long.valueOf(value).longValue();

	}

	private Long getNewId(Long oldId, Maps map) {
		Long newId = oldId;
		switch (map) {
		case USERS:
			if (usersMap.get(oldId) != null)
				newId = usersMap.get(oldId);
			break;
		case ORGANISATIONS:
			if (organisationsMap.get(oldId) != null)
				newId = organisationsMap.get(oldId);
			break;
		case APPOINTMENTS:
			if (appointmentsMap.get(oldId) != null)
				newId = appointmentsMap.get(oldId);
			break;
		case ROOMS:
			if (roomsMap.get(oldId) != null)
				newId = roomsMap.get(oldId);
			break;
		case MESSAGEFOLDERS:
			if (messageFoldersMap.get(oldId) != null)
				newId = messageFoldersMap.get(oldId);
			break;
		case USERCONTACTS:
			if (userContactsMap.get(oldId) != null)
				newId = userContactsMap.get(oldId);
			break;
		default:
			break;
		}
		return newId;
	}

}
