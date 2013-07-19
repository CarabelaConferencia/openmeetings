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
package org.apache.openmeetings.web.user.profile;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class UserInfoDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 6393565468567393270L;
	private WebMarkupContainer container;
	private DialogButton cancel = new DialogButton(WebSession.getString(61));
	private DialogButton message = new DialogButton(WebSession.getString(1253));
	private DialogButton contacts = new DialogButton(WebSession.getString(1186));
	
	public UserInfoDialog(String id) {
		super(id, WebSession.getString(1235));
		setOutputMarkupId(true);
		
		add(container = new WebMarkupContainer("container"));
		container.add(new WebMarkupContainer("body")).setOutputMarkupId(true);
	}
	
	public WebMarkupContainer getContainer() {
		return container;
	}
	
	@Override
	public int getWidth() {
		return 500;
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(contacts, message, cancel);
	}
	
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (button.equals(message)) {
			//TODO add code
		} else if (button.equals(contacts)) {
			//TODO add code
		}
	}
}