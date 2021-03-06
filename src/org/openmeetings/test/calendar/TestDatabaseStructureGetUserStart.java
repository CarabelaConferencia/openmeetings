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
package org.openmeetings.test.calendar;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureGetUserStart extends AbstractOpenmeetingsSpringTest {
	private static final Logger log = Logger.getLogger(TestDatabaseStructureGetUserStart.class);
	@Autowired
	private UsersDaoImpl usersDao;
	
	@Test
	public void testAddingGroup() {
		try {
			usersDao.getUser(new Long(1));
		} catch (Exception err) {
			log.error("[testAddingGroup]", err);
		}
	}
}
