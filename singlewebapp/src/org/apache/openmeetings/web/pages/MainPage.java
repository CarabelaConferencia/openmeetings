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
package org.apache.openmeetings.web.pages;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.util.UrlFragment.CHILD_ID;
import static org.apache.openmeetings.web.util.UrlFragment.DASHBOARD;
import static org.apache.openmeetings.web.util.UrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.UrlFragment.PROFILE_MESSAGES;
import static org.apache.openmeetings.web.util.UrlFragment.getPanel;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.openmeetings.web.common.MenuPanel;
import org.apache.openmeetings.web.user.AboutDialog;
import org.apache.openmeetings.web.user.ChatPanel;
import org.apache.openmeetings.web.util.UrlFragment;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

@AuthorizeInstantiation("USER")
public class MainPage extends BasePage {
	private static final long serialVersionUID = 6421960759218157999L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPage.class, webAppRootKey);
	private final MenuPanel menu;
	private final MarkupContainer contents;
	private final AbstractAjaxTimerBehavior areaBehavior;
	
	public MainPage() {
		contents = new WebMarkupContainer("contents");
		add(contents.add(new WebMarkupContainer(CHILD_ID)).setOutputMarkupId(true).setMarkupId("contents"));
		menu = new MenuPanel("menu");
		add(menu);
		add(new AjaxLink<Void>("messages") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_EDIT, target);
			}
		});
		add(new ConfirmableAjaxLink("logout", 634L) {
			private static final long serialVersionUID = -2994610981053570537L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		add(new AjaxLink<Void>("profile") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_MESSAGES, target);
			}
		});
		final AboutDialog about = new AboutDialog("aboutDialog");
		add(new AjaxLink<Void>("about") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				about.open(target);
			}
		});
		add(about);
		add(new ExternalLink("bug", "https://issues.apache.org/jira/browse/OPENMEETINGS"));//FIXME hardcoded
		
		add(new ChatPanel("chatPanel"));
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = -3311970325911992958L;

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				log.debug("WebSocketBehavior::onConnect");
			}
			
			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				log.debug("WebSocketBehavior::onClose");
			}
		});
		//load preselected content
		add(areaBehavior = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
			private static final long serialVersionUID = -1551197896975384329L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				UrlFragment area = WebSession.get().getArea();
				updateContents(area == null ? DASHBOARD : area, target);
				stop(target);
				WebSession.get().setArea(null);
			}
		});
	}
	
	public void updateContents(UrlFragment f, AjaxRequestTarget target) {
		BasePanel panel = getPanel(f.getArea(), f.getType());
		if (panel != null) {
			target.add(contents.replace(panel));
			//FIXME need to resolve name conflict
			//FIXME need to implement 1 call instead of 2
			org.wicketstuff.urlfragment.UrlFragment uf = new org.wicketstuff.urlfragment.UrlFragment(target);
			//uf.set("");
			uf.setParameter(f.getArea().name(), f.getType());
			panel.onMenuPanelLoad(target);
		}
	}
	
	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget target) {
		UrlFragment uf = getUrlFragment(params);
		if (uf != null) {
			areaBehavior.stop(target);
			updateContents(uf, target);
		}
	}
}
