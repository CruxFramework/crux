/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.clientoffline;

import org.cruxframework.crux.core.client.event.BaseEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class NetworkEvent extends BaseEvent<Network>
{
	/**
	 * Implemented by objects that handle {@link NetworkEvent}.
	 */
	public interface Handler extends EventHandler
	{
		void onNetworkChanged(NetworkEvent event);
	}

	private final boolean onLine;

	/**
	 * Construct a new {@link NetworkEvent}.
	 * 
	 * @param onLine true if the application is online
	 */
	protected NetworkEvent(boolean onLine)
	{
		super (Network.get());
		this.onLine = onLine;
	}

	/**
	 * Returns true if this event announces that the source has been onLine,
	 * false if it has been online.
	 */
	public boolean isOnLine()
	{
		return onLine;
	}
}
