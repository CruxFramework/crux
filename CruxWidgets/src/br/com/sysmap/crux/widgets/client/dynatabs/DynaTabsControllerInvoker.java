/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.dynatabs;

import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.js.JSWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;

/**
 * @author Gessé Dafé <code>gessedafe@gmail.com</code>
 */
public class DynaTabsControllerInvoker 
{
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
	public static <T> T invokeOnSiblingTab(String tabId, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{		
		Element tabIFrame = getSiblingTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoSiblingTabFound(tabId));
		}
	
		return retrieveTabWindowAndInvoke(call, param, tabIFrame, resultType);
	}
	
	/**
	 * Invokes a method on a sibling tab controller 
	 * @param tabId
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	public static void invokeOnSiblingTab(String tabId, String call, Object param) throws ModuleComunicationException
	{		
		Element tabIFrame = getSiblingTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoSiblingTabFound(tabId));
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
	public static <T> T invokeOnTab(String tabId, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{		
		Element tabIFrame = getTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoTabFound(tabId));
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
	public static void invokeOnTab(String tabId, String call, Object param) throws ModuleComunicationException
	{		
		Element tabIFrame = getTabInternalFrameElement(tabId);
		if(tabIFrame == null)
		{
			throw new ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoTabFound(tabId));
		}
	
		retrieveTabWindowAndInvoke(call, param, tabIFrame);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T retrieveTabWindowAndInvoke(String call, Object param, Element tabIFrame, Class<T> resultType) throws ModuleComunicationException
	{
		TabInternalJSObjects tabObjetcs = GWT.create(TabInternalJSObjectsImpl.class);
		JSWindow window = tabObjetcs.getTabWindow((IFrameElement) tabIFrame);
		String result = callSiblingTabControllerAccessor(window, call,  Screen.getCruxSerializer().serialize(param));
		return (T) Screen.getCruxSerializer().deserialize(result);
	}
	
	private static void retrieveTabWindowAndInvoke(String call, Object param, Element tabIFrame) throws ModuleComunicationException
	{
		TabInternalJSObjects tabObjetcs = GWT.create(TabInternalJSObjectsImpl.class);
		JSWindow window = tabObjetcs.getTabWindow((IFrameElement) tabIFrame);
		callSiblingTabControllerAccessor(window, call,  Screen.getCruxSerializer().serialize(param));
	}
	
	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	private static native String callSiblingTabControllerAccessor(JSWindow tabWindow, String call, String serializedData)/*-{
		return tabWindow._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	private static native Element getSiblingTabInternalFrameElement(String tabId)/*-{
		try
		{
			return $wnd.parent.document.getElementById(tabId + '.window');
		}
		catch(e)
		{
			return null;
		}
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @param string 
	 * @return
	 */
	private static Element getTabInternalFrameElement(String tabId)
	{
		return DOM.getElementById(tabId + ".window");
	}
}