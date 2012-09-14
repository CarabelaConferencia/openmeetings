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
package org.apache.openmeetings.web.components.admin.servers;

import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.model.CompoundPropertyModel;

public class ServerForm extends AdminBaseForm<Server> {

	private static final long serialVersionUID = 1L;

	public ServerForm(String id, final Server server) {
		super(id, new CompoundPropertyModel<Server>(server));
		setOutputMarkupId(true);
		
	}
}