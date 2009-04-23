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
package br.com.sysmap.crux.core.client.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Abstraction for the entire page. It encapsulate all the components, containers and 
 * datasources.
 * @author Thiago
 *
 */
public class Screen
{
	protected String id;
	protected Map<String, Component> components = new HashMap<String, Component>(30);
	protected Element blockDiv;
	protected boolean manageHistory = false;
	protected IFrameElement historyFrame = null;
	protected List<ScreenLoadHandler> loadHandlers = new ArrayList<ScreenLoadHandler>();
		
	
	
	public Screen(String id) 
	{
		this.id = id;
	}
	
	public String getId() 
	{
		return id;
	}
	
	public boolean isManageHistory() {
		return manageHistory;
	}

	public void setManageHistory(boolean manageHistory) {
		if (this.manageHistory != manageHistory)
		{
			this.manageHistory = manageHistory;
			Element body = RootPanel.getBodyElement();
			if (manageHistory)
			{
				if (historyFrame == null)
				{
					historyFrame = DOM.createIFrame().cast();
					historyFrame.setSrc("javascript:''");
					historyFrame.setId("__gwt_historyFrame");
					historyFrame.getStyle().setProperty("width", "0");
					historyFrame.getStyle().setProperty("height", "0");
					historyFrame.getStyle().setProperty("border", "0");
					body.appendChild(historyFrame);
				}
			}
			else
			{
				if (historyFrame != null)
				{
					body.removeChild(historyFrame);
					historyFrame = null;
				}			
			}
		}
	}

	public Component getComponent(String id)
	{
		return components.get(id);
	}
	
	void addComponent(Component component)
	{
		components.put(component.getId(), component);
		component.setScreen(this);
	}

	public void blockToUser()
	{
		if (blockDiv == null)
		{
			blockDiv = DOM.createDiv();
			blockDiv.getStyle().setProperty("position","absolute");
			blockDiv.getStyle().setPropertyPx("top", 0);
			blockDiv.getStyle().setPropertyPx("left", 0);
			blockDiv.getStyle().setPropertyPx("height", Window.getClientHeight());
			blockDiv.getStyle().setPropertyPx("width", Window.getClientWidth());
			blockDiv.getStyle().setProperty("zIndex", "99999");
			blockDiv.getStyle().setProperty("backgroundColor", "white");
			blockDiv.getStyle().setProperty("opacity", ".01");
			blockDiv.getStyle().setProperty("cursor", "wait");
			blockDiv.getStyle().setProperty("filter", "alpha(opacity=1)");
			Element body = RootPanel.getBodyElement();
			body.appendChild(blockDiv);
			body.getStyle().setProperty("cursor", "wait");
		}
	}
	
	public void unblockToUser()
	{
		if (blockDiv != null)
		{
			Element body = RootPanel.getBodyElement();
			body.removeChild(blockDiv);
			blockDiv = null;
			body.getStyle().setProperty("cursor", "");
		}
	}
	
	protected void parse(Element element) 
	{
		String manageHistoryStr = element.getAttribute("_manageHistory");
		if (manageHistoryStr != null)
		{
			setManageHistory("true".equals(manageHistoryStr));
		}
		final Event eventClosing = EventFactory.getEvent(EventFactory.EVENT_CLOSING, element.getAttribute(EventFactory.EVENT_CLOSING));
		if (eventClosing != null)
		{
			Window.addWindowClosingHandler(new Window.ClosingHandler(){
				public void onWindowClosing(ClosingEvent closingEvent) 
				{
					EventFactory.callEvent(eventClosing, getId());
				}
			});
		}

		final Event eventClose = EventFactory.getEvent(EventFactory.EVENT_CLOSE, element.getAttribute(EventFactory.EVENT_CLOSE));
		if (eventClose != null)
		{
			Window.addCloseHandler(new CloseHandler<Window>(){
				public void onClose(CloseEvent<Window> event) 
				{
					EventFactory.callEvent(eventClose, getId());				}
			});
		}

		final Event eventResized = EventFactory.getEvent(EventFactory.EVENT_RESIZED, element.getAttribute(EventFactory.EVENT_RESIZED));
		if (eventResized != null)
		{
			Window.addResizeHandler(new ResizeHandler(){
				@Override
				public void onResize(ResizeEvent event) 
				{
					EventFactory.callEvent(eventResized, getId());
				}
			});
		}
		final Event eventLoad = EventFactory.getEvent(EventFactory.EVENT_LOAD, element.getAttribute(EventFactory.EVENT_LOAD));
		if (eventLoad != null)
		{
			addLoadHandler(new ScreenLoadHandler(){
				@Override
				public void onLoad() 
				{
					EventFactory.callEvent(eventLoad, getId());
				}
			});
		}
	}

	/**
	 * Adds an event handler that is called only once, when the screen is loaded
	 * @param handler
	 */
	void addLoadHandler(final ScreenLoadHandler handler) 
	{
		loadHandlers.add(handler);
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	void fireLoadEvent() 
	{
		if (loadHandlers.size() > 0)
		{
			new Timer()
			{
				public void run() 
				{
					for (final ScreenLoadHandler handler : loadHandlers) 
					{
						try 
						{
							DeferredCommand.addCommand(new Command() {
						        public void execute() 
						        {
						        	handler.onLoad();
						        }
						      });							
						} 
						catch (RuntimeException e) 
						{
							GWT.log(e.getLocalizedMessage(), e);
						}
					}
					loadHandlers.clear();
				}
			}.schedule(1); // Waits for browser starts the rendering process
		}
	}
}
