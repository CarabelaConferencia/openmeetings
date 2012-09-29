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
package org.apache.openmeetings.data.user;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.Fieldmanagment;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.persistence.beans.adresses.Adresses;
import org.apache.openmeetings.templates.RegisterUserTemplate;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Emailmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Emailmanagement.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ConfigurationDaoImpl configurationDaoImpl;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private RegisterUserTemplate registerUserTemplate;

	/**
	 * sends a mail adress to the user with his account data
	 * 
	 * @param Username
	 * @param Userpass
	 * @param EMail
	 * @return
	 * @throws Exception
	 */
	public String sendMail(String Username, String Userpass, String EMail,
			String link, Boolean sendEmailWithVerficationCode) {
		String succ = "valid email";

		Integer sendEmailAtRegister = Integer.valueOf(
				configurationDaoImpl.getConfKey("sendEmailAtRegister")
						.getConf_value()).intValue();

		if (sendEmailAtRegister == 1) {

			Long default_lang_id = Long.valueOf(
					configurationDaoImpl.getConfKey("default_lang_id")
							.getConf_value()).longValue();

			if (sendEmailWithVerficationCode) {

				String verification_url = link;

				String template = registerUserTemplate
						.getRegisterUserWithVerificationTemplate(Username,
								Userpass, EMail, default_lang_id,
								verification_url);

				succ = mailHandler.sendMail(EMail, fieldmanagment.getString(512L, default_lang_id), template);

			} else {

				String template = registerUserTemplate
						.getRegisterUserTemplate(Username, Userpass, EMail,
								default_lang_id);

				succ = mailHandler.sendMail(EMail, fieldmanagment.getString(512L, default_lang_id), template);
			}

			return succ;
		} else {
			return "success";
		}
	}

	public String addEmailCon(String EMail, int CONTACT_ID) {
		String succ = "invalid email";
		return succ;
	}

	/**
	 * Checks if a mail is already taken by someone else
	 * 
	 * @param email
	 * @return
	 */
	public boolean checkUserEMail(String email) {
		try {
			if (email.length() == 0)
				return true;
			log.debug("checkUserMail: " + email);
			TypedQuery<Adresses> query = em
					.createQuery("select c from Adresses as c where c.email LIKE :email AND c.deleted <> :deleted", Adresses.class);
			query.setParameter("email", email);
			query.setParameter("deleted", true);
			int count = query.getResultList().size();
			log.debug("size: " + count);

			if (count > 0) {
				return false;
			}
		} catch (Exception ex2) {
			log.error("checkUserEMail: ", ex2);
		}
		return true;
	}
}
