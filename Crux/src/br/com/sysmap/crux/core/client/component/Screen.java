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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.JSEngine;
import br.com.sysmap.crux.core.client.component.Serializer.SerializationException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker;
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstraction for the entire page. It encapsulate all the widgets, containers and 
 * datasources.
 * @author Thiago
 *
 */
public class Screen
{
	protected String id;
	protected Map<String, Widget> widgets = new HashMap<String, Widget>(30);
	protected Element blockDiv;
	protected boolean manageHistory = false;
	protected IFrameElement historyFrame = null;
	protected HandlerManager handlerManager;
	
	public Screen(String id) 
	{
		this.id = id;
		this.handlerManager = new HandlerManager(this);
		createControllerAccessor(this);
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

	public Widget getWidget(String id)
	{
		return widgets.get(id);
	}
	
	/**
	 * Generic version of <code>getWidget</code> method
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String id, Class<T> clazz)
	{
		Widget w = widgets.get(id);
		return (T) w;
	}
	
	public void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}
	
	public void removeWidget(String id)
	{
		removeWidget(id, true);
	}

	public void removeWidget(String id, boolean removeFromDOM)
	{
		Widget widget = widgets.remove(id);
		if (widget != null && removeFromDOM)
		{
			widget.removeFromParent();
		}
	}

	public Iterator<String> iteratorWidgetsIds()
	{
		return widgets.keySet().iterator();
	}
	
	public Iterator<Widget> iteratorWidgets()
	{
		return widgets.values().iterator();
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
		String title = element.getAttribute("_title");
		if (title != null && title.length() >0)
		{
			Window.setTitle(ScreenFactory.getInstance().getDeclaredMessage(title));
		}
		final Event eventClosing = EventFactory.getEvent(EventFactory.EVENT_CLOSING, element.getAttribute(EventFactory.EVENT_CLOSING));
		if (eventClosing != null)
		{
			Window.addWindowClosingHandler(new Window.ClosingHandler(){
				public void onWindowClosing(ClosingEvent closingEvent) 
				{
					EventFactory.callEvent(eventClosing, closingEvent);
				}
			});
		}

		final Event eventClose = EventFactory.getEvent(EventFactory.EVENT_CLOSE, element.getAttribute(EventFactory.EVENT_CLOSE));
		if (eventClose != null)
		{
			Window.addCloseHandler(new CloseHandler<Window>(){
				public void onClose(CloseEvent<Window> event) 
				{
					EventFactory.callEvent(eventClose, event);				
				}
			});
		}

		final Event eventResized = EventFactory.getEvent(EventFactory.EVENT_RESIZED, element.getAttribute(EventFactory.EVENT_RESIZED));
		if (eventResized != null)
		{
			Window.addResizeHandler(new ResizeHandler(){
				public void onResize(ResizeEvent event) 
				{
					EventFactory.callEvent(eventResized, event);
				}
			});
		}
		final Event eventLoad = EventFactory.getEvent(EventFactory.EVENT_LOAD, element.getAttribute(EventFactory.EVENT_LOAD));
		if (eventLoad != null)
		{
			addLoadHandler(new ScreenLoadHandler(){
				public void onLoad(ScreenLoadEvent screenLoadEvent) 
				{
					EventFactory.callEvent(eventLoad, screenLoadEvent);
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
		handlerManager.addHandler(ScreenLoadEvent.TYPE, handler);
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	void load() 
	{
		if (handlerManager.getHandlerCount(ScreenLoadEvent.TYPE) > 0)
		{
			new Timer()
			{
				public void run() 
				{
					try 
					{
						DeferredCommand.addCommand(new Command() {
							public void execute() 
							{
								ScreenLoadEvent.fire(Screen.this);
							}
						});							
					} 
					catch (RuntimeException e) 
					{
						GWT.log(e.getLocalizedMessage(), e);
					}
				}
			}.schedule(1); // Waits for browser starts the rendering process
		}
	}

		/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	void fireEvent(ScreenLoadEvent event) 
	{
		handlerManager.fireEvent(event);
	}
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * @param eventHandler
	 */
	public void updateScreenWidgets(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof EventClientHandlerInvoker))
			{
				throw new ClassCastException(JSEngine.messages.screenInvalidHandlerError());
			}

			((EventClientHandlerInvoker) eventHandler).updateScreenWidgets();
		}
	}
	
	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * @param eventHandler
	 */
	public void updateControllerObjects(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof EventClientHandlerInvoker))
			{
				throw new ClassCastException(JSEngine.messages.screenInvalidHandlerError());
			}
			((EventClientHandlerInvoker) eventHandler).updateControllerObjects();

		}
	}
	
	/**
	 * Gets the current screen
	 * @return
	 */
	public static Screen get()
	{
		return ScreenFactory.getInstance().getScreen();
	}
	
	/**
	 * Gets a widget on the current screen
	 * @param id
	 * @return
	 */
	public static Widget get(String id)
	{
		return Screen.get().getWidget(id);
	}
	
	/**
	 * Generic version of <code>getWidget</code> method
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Widget> T get(String id, Class<T> clazz)
	{
		return Screen.get().getWidget(id, clazz);
	}
	
	/**
	 * Remove a widget on the current screen
	 * @param id
	 */
	public static void remove(String id)
	{
		Screen.get().removeWidget(id);
	}
	
	/**
	 * Remove a widget on the current screen
	 * @param id
	 * @param removeFromDOM
	 */
	public static void remove(String id, boolean removeFromDOM)
	{
		Screen.get().removeWidget(id, removeFromDOM);
	}
	
	private native void createControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = function(call, serializedData){
			handler.@br.com.sysmap.crux.core.client.component.Screen::invokeController(Ljava/lang/String;Ljava/lang/String;)(call, serializedData);
		};
	}-*/;
	
	
	@SuppressWarnings("unused") // called by native code
	private void invokeController(String call, String serializedData)
	{
		Event event = EventFactory.getEvent("_onInvokeController", call);
		InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
		if (serializedData != null)
		{
			try
			{
				controllerEvent.setData(Serializer.deserialize(serializedData));
			}
			catch (SerializationException e)
			{
				GWT.log(e.getLocalizedMessage(), e);
				Window.alert(e.getLocalizedMessage());
			}
		}
		
		EventFactory.callEvent(event, controllerEvent);		
	}
	
	private native void callTopControllerAccessor(String call, String serializedData)/*-{
		$wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	public void invokeControllerOnTop(String call) throws SerializationException
	{
		invokeControllerOnTop(call, null);
	}

	public void invokeControllerOnTop(String call, Object param) throws SerializationException
	{
		callTopControllerAccessor(call, Serializer.serialize(param));
	}
}
