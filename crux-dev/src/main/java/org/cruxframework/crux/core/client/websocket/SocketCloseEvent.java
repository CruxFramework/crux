/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.client.websocket;

import org.cruxframework.crux.core.client.event.CruxEvent;

public class SocketCloseEvent extends CruxEvent<WebSocket> 
{
	private boolean wasClean;
	private String reason;
	private short code;

	/**
	 * 
	 */
	protected SocketCloseEvent(WebSocket socket, boolean wasClean, short code, String reason)
	{
		super(socket, socket.getUrl());
		this.wasClean = wasClean;
		this.code = code;
		this.reason = reason;
	}

	public boolean isWasClean() 
	{
		return wasClean;
	}

	public String getReason() 
	{
		return reason;
	}

	public short getCode() 
	{
		return code;
	}
}
