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
package org.red5.screen.webstart;

import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmp.ClientExceptionHandler;
import org.red5.server.net.rtmp.RTMPClient;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.message.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenShare extends RTMPClient implements ClientExceptionHandler, IScreenShare {

	private static final Logger logger = LoggerFactory
			.getLogger(ScreenShare.class);

	private CommonScreenShare instance = null;

	private ScreenShare() {
		instance = new CommonScreenShare(this);
	};

	public static void main(String[] args) {
		new ScreenShare().instance.main(args);
	}
	
	// ------------------------------------------------------------------------
	//
	// Override
	//
	// ------------------------------------------------------------------------
	@Override
	public void connectionOpened(RTMPConnection conn, RTMP state) {
		logger.debug("connection opened");
		super.connectionOpened(conn, state);
		this.conn = conn;
	}

	@Override
	public void connectionClosed(RTMPConnection conn, RTMP state) {
		logger.debug("connection closed");
		super.connectionClosed(conn, state);
	}

	@Override
	protected void onInvoke(RTMPConnection conn, Channel channel,
			Header source, Notify invoke, RTMP rtmp) {
		super.onInvoke(conn, channel, source, invoke, rtmp);

		instance.onInvoke(conn, channel, source, invoke, rtmp);
	}

	@Override
	public void handleException(Throwable throwable) {
		logger.error("{}", new Object[] { throwable.getCause() });
		System.out.println(throwable.getCause());
	}

	public void onStreamEvent(Notify notify) {
		instance.onStreamEvent(notify);
	}

	public void resultReceived(IPendingServiceCall call) {
		instance.resultReceived(call);
	}
}
