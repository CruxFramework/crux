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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleElement;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceDisplayHandler
{
	private static DisplayHandler displayHandler = null;
	
	/**
	 * Normalize the zoom level to ensure that the page will be displayed with the same relative width on different devices.
	 */
	public static void configureDisplayForDevice()
	{
		DisplayHandler displayHandler = getDisplayHandler(); 
		displayHandler.configureScreenZoom();
	}
	
	public static double getScreenZoomFactor()
	{
		DisplayHandler displayHandler = getDisplayHandler(); 
		return displayHandler.getScreenZoomFactor();
	}

	private static DisplayHandler getDisplayHandler()
    {
		if (displayHandler == null)
		{
			displayHandler = GWT.create(DisplayHandler.class);
		}
		return displayHandler;
    }

	static class DisplayHandler
	{
		void configureScreenZoom()
		{
			
		}
		
		double getScreenZoomFactor()
		{
			return 1;
		}
	}
	
	static class SmallDisplayHandler extends DisplayHandler
	{
		@Override
		void configureScreenZoom()
		{
			double zoomFactor = getScreenZoomFactor();
			if (zoomFactor != 1)
			{
				StyleContentInjector styleContentInjector = GWT.create(StyleContentInjector.class);
				createStyleElementForScreenZoom(styleContentInjector, zoomFactor);
			}
		}
		
		@Override
		double getScreenZoomFactor()
		{
			double pixelRatio = getDevicePixelRatio();
			if (pixelRatio > 0)
			{
				if (pixelRatio >= 2)
				{
					return 0.667;
				}
				else if (pixelRatio >= 1.5)
				{
					return 1;
				}
				else if (pixelRatio >= 1.0)
				{
					return 0.667;
				}
				else if (pixelRatio < 1.0)
				{
					return 0.5;
				}
			}
			return 1;
		}
		
		/**
		 * 
		 * @return
		 */
		private native double getDevicePixelRatio()/*-{
			if ($wnd.devicePixelRatio)
			{
				return $wnd.devicePixelRatio;
			}
			return -1;
		}-*/;
		
		/**
		 * 
		 * @param styleContentInjector
		 * @param zoomFactor
		 */
		private void createStyleElementForScreenZoom(StyleContentInjector styleContentInjector, double zoomFactor)
	    {
		    StyleElement styleElement = Document.get().createStyleElement();
			styleElement.setType("text/css");
			styleContentInjector.injectContent(styleElement,"HTML{zoom: "+zoomFactor+";}");
			
			Document.get().getElementsByTagName("head").getItem(0).appendChild(styleElement);
	    }
	}
	
	static class StyleContentInjector
	{
		void injectContent(StyleElement element, String content)
		{
			element.setInnerText(content);
		}
	}

	static class IEStyleContentInjector extends StyleContentInjector
	{
		void injectContent(StyleElement element, String content)
		{
			element.setCssText(content);
		}
	}	
}
