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
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseHandler;
import br.com.sysmap.crux.widgets.client.event.openclose.HasBeforeCloseHandlers;
import br.com.sysmap.crux.widgets.client.js.JSWindow;
import br.com.sysmap.crux.widgets.client.util.FrameStateCallback;
import br.com.sysmap.crux.widgets.client.util.FrameUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractTab extends Widget implements HasBeforeCloseHandlers
{
	private String id;
	private String url;
	private String label;
	private Frame frame;
	private TabInternalJSObjects tabObjetcs;
	private boolean canClose = false;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param label
	 * @param url
	 * @param closeable
	 */
	protected AbstractTab(String id, String label, String url)
	{
		this.id = id;
		this.label = label;
		this.url = url;
		
		this.frame = new Frame(url);
		this.frame.setHeight("100%");
		Element frameElement = getElement();
		frameElement.setPropertyString("frameBorder", "no");
		frameElement.setPropertyString("border", "0");
		frameElement.setPropertyString("id", id + ".window");
		frameElement.setPropertyString("name", id + ".window");

		FrameUtils.registerStateCallback(frameElement, new FrameStateCallback(){
			public void onComplete()
			{
				canClose = true;
			}
		}, 20000);
		
		tabObjetcs = GWT.create(TabInternalJSObjectsImpl.class);
	}

	protected void executeWhenLoaded(final Command call)
	{
		FrameUtils.registerStateCallback(getElement(), new FrameStateCallback(){
			public void onComplete()
			{
				canClose = true;
				call.execute();
			}
		}, 20000);
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	public static <T> T invokeOnSiblingTab(String tabId, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return CruxInternalDynaTabsController.invokeOnSiblingTab(tabId, call, param, resultType);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	public static void invokeOnSiblingTab(String tabId, String call, Object param) throws ModuleComunicationException
	{
		CruxInternalDynaTabsController.invokeOnSiblingTab(tabId, call, param);
	}

	/**
	 * @return the url
	 */
	protected String getUrl()
	{
		return url;
	}

	/**
	 * 
	 * @return
	 */
	protected boolean canClose()
	{
		return canClose;
	}
	
	/**
	 * @return the frame
	 */
	protected Frame getFrame()
	{
		return frame;
	}

	@Override
	public Element getElement()
	{
		return frame.getElement();
	}

	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
	{
		return addHandler(handler, BeforeCloseEvent.getType());
	}
	
	public JSWindow getInternalWindow()
	{
		return tabObjetcs.getTabWindow(getFrame().getElement().<IFrameElement> cast());
	}
	
	public Document getInternalDocument()
	{
		return tabObjetcs.getTabDocument(getFrame().getElement().<IFrameElement> cast());
	}
}