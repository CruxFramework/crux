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
package org.cruxframework.crux.widgets.client.dynatabs;

import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.ModuleComunicationException;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;

/**
 * @author Gesse S. F. Dafe
 */
public class DynaTabsControllerInvoker 
{
	private static final String TAB_FRAME_SUFFIX = ".window";
	
	/**
	 * @param tabId the tab identifier
	 * @return the tab window object
	 */
	public static JSWindow getSiblingTabWindow(String tabId)
	{
		Element tabIFrame = getSiblingTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			return null;
		}
		return retrieveTabWindow(tabIFrame);
	}
	
	/**
	 * @param tabId the tab identifier
	 * @return the tab window object
	 */
	public static JSWindow getTabWindow(String tabId)
	{
		Element tabIFrame = getTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			return null;
		}
		return retrieveTabWindow(tabIFrame);
	}

	/**
	 * Invokes a method on a sibling tab controller 
	 * @param tabId
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnSiblingTab(String tabId, String call, Object param) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{		
		Element tabIFrame = getSiblingTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new org.cruxframework.crux.core.client.screen.ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoSiblingTabFound(tabId));
		}
	
		retrieveTabWindowAndInvoke(call, param, tabIFrame);
	}

	/**
	 * Invokes a method on a sibling tab controller 
	 * @param <T>
	 * @param tabId
	 * @param call
	 * @param param
	 * @param resultType
	 * @return
	 * @throws ModuleComunicationException
	 */	
	@Deprecated
	public static <T> T invokeOnSiblingTab(String tabId, String call, Object param, Class<T> resultType) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{		
		Element tabIFrame = getSiblingTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new org.cruxframework.crux.core.client.screen.ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoSiblingTabFound(tabId));
		}
	
		return retrieveTabWindowAndInvoke(call, param, tabIFrame, resultType);
	}
	
	/**
	 * Invokes a method on a tab controller 
	 * @param tabId
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnTab(String tabId, String call, Object param) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{		
		Element tabIFrame = getTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new org.cruxframework.crux.core.client.screen.ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoTabFound(tabId));
		}
	
		retrieveTabWindowAndInvoke(call, param, tabIFrame);
	}
	
	/**
	 * Invokes a method on a tab controller 
	 * @param <T>
	 * @param tabId
	 * @param call
	 * @param param
	 * @param resultType
	 * @return
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static <T> T invokeOnTab(String tabId, String call, Object param, Class<T> resultType) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{		
		Element tabIFrame = getTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new org.cruxframework.crux.core.client.screen.ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoTabFound(tabId));
		}
	
		return retrieveTabWindowAndInvoke(call, param, tabIFrame, resultType);
	}
	
	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	protected static Element getTabInternalFrameElement(String tabId)
	{
		return DOM.getElementById(tabId + TAB_FRAME_SUFFIX);
	}
	
	protected static JSWindow retrieveTabWindow(Element tabIFrame)
    {
	    TabInternalJSObjects tabObjetcs = GWT.create(TabInternalJSObjectsImpl.class);
		JSWindow window = tabObjetcs.getTabWindow((IFrameElement) tabIFrame);
	    return window;
    }

	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	@Deprecated
	private static native String callSiblingTabControllerAccessor(JSWindow tabWindow, String call, String serializedData)/*-{
		return tabWindow._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	public static native Element getSiblingTabInternalFrameElement(String tabId)/*-{
		try
		{
			return $wnd.parent.document.getElementById(tabId + '.window');
		}
		catch(e)
		{
			return null;
		}
	}-*/;
	
	@Deprecated
	private static void retrieveTabWindowAndInvoke(String call, Object param, Element tabIFrame) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{
		JSWindow window = retrieveTabWindow(tabIFrame);
		callSiblingTabControllerAccessor(window, call,  Screen.getCruxSerializer().serialize(param));
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	private static <T> T retrieveTabWindowAndInvoke(String call, Object param, Element tabIFrame, Class<T> resultType) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{
		JSWindow window = retrieveTabWindow(tabIFrame);
		String result = callSiblingTabControllerAccessor(window, call,  Screen.getCruxSerializer().serialize(param));
		return (T) Screen.getCruxSerializer().deserialize(result);
	}
}