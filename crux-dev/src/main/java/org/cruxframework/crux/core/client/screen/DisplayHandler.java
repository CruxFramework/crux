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
package org.cruxframework.crux.core.client.screen;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;

/**
 * Helper Class to configure display for different devices.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DisplayHandler
{
	private static AbstractViewPortHandler viewPortHandler = GWT.create(AbstractViewPortHandler.class);
	
	/**
	 * Configure a viewport with a default size.
	 */
	public static void configureViewport()
    {
		configureViewport("user-scalable=no, width=320", "user-scalable=no, width=device-width, height=device-height");
    }
	
	/**
	 * Configure a viewport using given contents for small and large displays
	 * @param smallDisplayContent
	 * @param largeDisplayContent
	 */
	public static void configureViewport(String smallDisplayContent, String largeDisplayContent)
	{
		if (Screen.getCurrentDevice().getSize().equals(Size.small))
		{
			configureViewport(smallDisplayContent);
		}
		else if (!Screen.getCurrentDevice().getInput().equals(Input.mouse))
		{
			configureViewport(largeDisplayContent);
		}
		
	}

	/**
	 * Configure a viewport using given contents
	 * @param content
	 */
	public static void configureViewport(String content)
    {
	    if (!StringUtils.isEmpty(content))
	    {
	    	createViewport(content);	    
	    }
    }

	/**
	 * Create a viewport meta element with specified content
	 * @param content
	 */
	public static void createViewport(String content)
	{
		createViewport(content, getCurrentWindow());
	}
	
	/**
	 * Create a viewport meta element with specified content
	 * @param content
	 * @param window
	 */
	public static void createViewport(String content, JavaScriptObject wnd)
	{
		viewPortHandler.createViewport(content, wnd);
	}

	private static native JavaScriptObject getCurrentWindow()/*-{
		return $wnd;
	}-*/;
}
