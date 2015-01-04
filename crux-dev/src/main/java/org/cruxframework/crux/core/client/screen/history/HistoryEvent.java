/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen.history;

import org.cruxframework.crux.core.client.event.BaseEvent;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class HistoryEvent extends BaseEvent<Screen> 
{

	private JavaScriptObject data;
	private String url;
	private String title;

	/**
	 * Constructor
	 * @param data History data object
	 * @param url History URL
	 * @param title History title
	 */
	protected HistoryEvent(JavaScriptObject data, String url, String title)
	{
		super(Screen.get());
		this.data = data;
		this.url = url;
		this.title = title;
	}

    @SuppressWarnings("unchecked")
    public <T extends JavaScriptObject> T getData()
	{
		return (T) data;
	}

	public String getUrl()
	{
		return url;
	}

	public String getTitle()
	{
		return title;
	}
}
