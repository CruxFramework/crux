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

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryUtils
{
	private static final String ENCLOSING_PANEL_PREFIX = "_crux_";
	private static final String LAZY_CHILDREN_PANEL_PREFIX = "_chld_";
	private static final String LAZY_PANEL_PREFIX = "_lazy_";
	private static final int LAZY_PANEL_PREFIX_LENGTH = LAZY_CHILDREN_PANEL_PREFIX.length();
	
	/**
	 * @param widgetId
	 * @param widget
	 */
	public static void updateWidgetElementId(String widgetId, Widget widget)
    {
	    Element element = widget.getElement();
	    if (StringUtils.isEmpty(element.getId()))
	    {
	    	element.setId(widgetId);
	    }
    }
	
	/**
	 * Returns the parent element (a wrapper span element) 
	 * @param widgetId
	 * @return
	 */
	public static Element getEnclosingPanelElement(String widgetId)
	{
		return DOM.getElementById(ENCLOSING_PANEL_PREFIX+widgetId);
	}	
	
	/**
	 * @return
	 */
	public static String getEnclosingPanelPrefix()
	{
		return ENCLOSING_PANEL_PREFIX;
	}	
	
	/**
	 * Return the id created to the panel that wraps the given widget id.
	 * @param wrappedWidgetId
	 * @param wrappingType 
	 * @return
	 */
	public static String getLazyPanelId(String wrappedWidgetId, LazyPanelWrappingType wrappingType)
	{
		if (wrappingType == LazyPanelWrappingType.wrapChildren)
		{
			return LAZY_CHILDREN_PANEL_PREFIX+wrappedWidgetId;
		}
		else
		{
			return LAZY_PANEL_PREFIX+wrappedWidgetId;
		}
	}
	
	/**
	 * Return the id of the widget wrapped by the given lazy panel id.
	 * @param lazyPanelId
	 * @return
	 */
	public static String getWrappedWidgetIdFromLazyPanel(String lazyPanelId)
	{
		assert(lazyPanelId != null && lazyPanelId.length() > LAZY_PANEL_PREFIX_LENGTH);
		return lazyPanelId.substring(LAZY_PANEL_PREFIX_LENGTH);
	}

	/**
	 * Check if the wrappedWidgetId is a valid lazy id generated for a {@code LazyPanelWrappingType.wrapChidren}
	 * lazy model 
	 * @param wrappedWidgetId
	 * @return
	 */
	public static boolean isChildrenWidgetLazyWrapper(String wrappedWidgetId)
	{
		assert(wrappedWidgetId != null);
		return wrappedWidgetId.startsWith(LAZY_CHILDREN_PANEL_PREFIX);
	}
	
	/**
	 * Check if the wrappedWidgetId is a valid lazy id generated for a {@code LazyPanelWrappingType.wrapWholeWidget}
	 * lazy model 
	 * @param wrappedWidgetId
	 * @return
	 */
	public static boolean isWholeWidgetLazyWrapper(String wrappedWidgetId)
	{
		assert(wrappedWidgetId != null);
		return wrappedWidgetId.startsWith(LAZY_PANEL_PREFIX);
	}
	
}
