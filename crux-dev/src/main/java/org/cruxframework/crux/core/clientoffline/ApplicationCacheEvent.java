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
import org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler.CacheEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class ApplicationCacheEvent extends BaseEvent<Network>
{
	/**
	 * Implemented by objects that handle {@link ApplicationCacheEvent}.
	 */
	public interface Handler extends EventHandler
	{
		void onCacheEvent(ApplicationCacheEvent event);
	}

	private final CacheEvent event;

	/**
	 * Construct a new {@link ApplicationCacheEvent}.
	 * 
	 * @param event current application cache event type
	 */
	protected ApplicationCacheEvent(Network network, CacheEvent event)
	{
		super (network);
		this.event = event;
	}

	/**
	 * Returns true application cache event type,
	 * false if it has been online.
	 */
	public CacheEvent getEventType()
	{
		return event;
	}
}
