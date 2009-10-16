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
package br.com.sysmap.crux.core.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.context.ContextManager;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.formatter.Formatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
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
	protected List<Element> blockingDivs = new ArrayList<Element>();
	protected IFrameElement historyFrame = null;
	protected HandlerManager handlerManager;
	protected ModuleComunicationSerializer serializer = null;
	protected ScreenBlocker screenBlocker = GWT.create(ScreenBlocker.class);
	
	protected Screen(String id) 
	{
		this.id = id;
		this.handlerManager = new HandlerManager(this);
		this.serializer = new ModuleComunicationSerializer();
		createControllerAccessor(this);
	}
	
	protected String getIdentifier() 
	{
		return id;
	}
	
	protected void prepareHistoryFrame() 
	{
		Element body = RootPanel.getBodyElement();
		if (historyFrame == null)
		{
			historyFrame = DOM.createIFrame().cast();
			historyFrame.setSrc("javascript:''");
			historyFrame.setId("__gwt_historyFrame");
			historyFrame.getStyle().setProperty("position", "absolute");
			historyFrame.getStyle().setProperty("width", "0");
			historyFrame.getStyle().setProperty("height", "0");
			historyFrame.getStyle().setProperty("border", "0");
			body.appendChild(historyFrame);
		    History.fireCurrentHistoryState();
		}
	}

	protected Widget getWidget(String id)
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
	protected <T extends Widget> T getWidget(String id, Class<T> clazz)
	{
		Widget w = widgets.get(id);
		return (T) w;
	}
	
	protected void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}
	
	protected void removeWidget(String id)
	{
		removeWidget(id, true);
	}

	protected void removeWidget(String id, boolean removeFromDOM)
	{
		Widget widget = widgets.remove(id);
		if (widget != null && removeFromDOM)
		{
			widget.removeFromParent();
		}
	}

	protected Iterator<String> iteratorWidgetsIds()
	{
		return widgets.keySet().iterator();
	}
	
	protected Iterator<Widget> iteratorWidgets()
	{
		return widgets.values().iterator();
	}

	/**
	 * Creates and shows a DIV over the screen contents
	 * @param blockingDivStyleName
	 */
	protected void showBlockDiv(String blockingDivStyleName)
	{
		if(blockingDivs.size() > 0)
		{
			Element blockingDiv = blockingDivs.get(blockingDivs.size() - 1);
			blockingDiv.getStyle().setProperty("display", "none");
		}
		
		Element body = RootPanel.getBodyElement();		
		Element blockingDiv = screenBlocker.createBlockingDiv(blockingDivStyleName, body);
		blockingDivs.add(blockingDiv);
		body.appendChild(blockingDiv);
	}

	/**
	 * Hides the DIV that is blocking the Screen contents
	 */
	protected void hideBlockDiv()
	{
		if(blockingDivs.size() > 0)
		{
			int last = blockingDivs.size() - 1;
			
			Element blockingDiv = blockingDivs.get(last);
			blockingDivs.remove(last);
			
			Element body = RootPanel.getBodyElement();
			body.removeChild(blockingDiv);
			body.getStyle().setProperty("cursor", "");
		}
		
		if(blockingDivs.size() > 0)
		{
			Element blockingDiv = blockingDivs.get(blockingDivs.size() - 1);
			blockingDiv.getStyle().setProperty("display", "block");
		}
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowClosingHandler(ClosingHandler handler) 
	{
		return Window.addWindowClosingHandler(handler);
	}	
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowCloseHandler(CloseHandler<Window> handler) 
	{
		return Window.addCloseHandler(handler);
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowResizeHandler(ResizeHandler handler) 
	{
		return Window.addResizeHandler(handler);
	}	
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowHistoryChangedHandler(ValueChangeHandler<String> handler) 
	{
		if (historyFrame == null)
		{
			prepareHistoryFrame();
		}
		return History.addValueChangeHandler(handler);
	}	

	/**
	 * 
	 * @param token
	 */
	protected void addTokenToHistory(String token)
	{
		History.newItem(token);
	}

	/**
	 * 
	 * @param token
	 * @param issueEvent
	 */
	protected void addTokenToHistory(String token, boolean issueEvent)
	{
		History.newItem(token, issueEvent);
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getCurrentHistoryToken()
	{
		return History.getToken();
	}
	
	/**
	 * 
	 * @param element
	 */
	protected void parse(Element element) 
	{
		String title = element.getAttribute("_title");
		if (title != null && title.length() >0)
		{
			Window.setTitle(ScreenFactory.getInstance().getDeclaredMessage(title));
		}

		final Event eventHistory = Events.getEvent(Events.EVENT_HISTORY_CHANGED, element.getAttribute("_" + Events.EVENT_HISTORY_CHANGED));
		if (eventHistory != null)
		{
			addWindowHistoryChangedHandler(new ValueChangeHandler<String>(){
				public void onValueChange(ValueChangeEvent<String> historyEvent)
				{
					Events.callEvent(eventHistory, historyEvent);
				}
			});
		}

		final Event eventClosing = Events.getEvent(Events.EVENT_CLOSING, element.getAttribute("_" + Events.EVENT_CLOSING));
		if (eventClosing != null)
		{
			addWindowClosingHandler(new Window.ClosingHandler(){
				public void onWindowClosing(ClosingEvent closingEvent) 
				{
					Events.callEvent(eventClosing, closingEvent);
				}
			});
		}

		final Event eventClose = Events.getEvent(Events.EVENT_CLOSE, element.getAttribute("_" + Events.EVENT_CLOSE));
		if (eventClose != null)
		{
			addWindowCloseHandler(new CloseHandler<Window>(){
				public void onClose(CloseEvent<Window> event) 
				{
					Events.callEvent(eventClose, event);				
				}
			});
		}

		final Event eventResized = Events.getEvent(Events.EVENT_RESIZED, element.getAttribute("_" + Events.EVENT_RESIZED));
		if (eventResized != null)
		{
			addWindowResizeHandler(new ResizeHandler(){
				public void onResize(ResizeEvent event) 
				{
					Events.callEvent(eventResized, event);
				}
			});
		}
		final Event eventLoad = Events.getEvent(Events.EVENT_LOAD, element.getAttribute("_" + Events.EVENT_LOAD));
		if (eventLoad != null)
		{
			addLoadHandler(new ScreenLoadHandler(){
				public void onLoad(ScreenLoadEvent screenLoadEvent) 
				{
					Events.callEvent(eventLoad, screenLoadEvent);
				}
			});
		}
	}

	/**
	 * Adds an event handler that is called only once, when the screen is loaded
	 * @param handler
	 */
	protected void addLoadHandler(final ScreenLoadHandler handler) 
	{
		handlerManager.addHandler(ScreenLoadEvent.TYPE, handler);
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	protected void load() 
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
						Crux.getErrorHandler().handleError(e);
					}
				}
			}.schedule(1); // Waits for browser starts the rendering process
		}
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	protected void fireEvent(ScreenLoadEvent event) 
	{
		handlerManager.fireEvent(event);
	}
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * @param caller
	 */
	protected void updateScreenWidgets(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof ScreenBindableObject))
			{
				throw new ClassCastException(Crux.getMessages().screenInvalidObjectError());
			}

			((ScreenBindableObject) eventHandler).updateScreenWidgets();
		}
	}
	
	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * @param caller
	 */
	protected void updateControllerObjects(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof ScreenBindableObject))
			{
				throw new ClassCastException(Crux.getMessages().screenInvalidObjectError());
			}
			((ScreenBindableObject) eventHandler).updateControllerObjects();

		}
	}

	private native void createControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = function(call, serializedData){
			var a = handler.@br.com.sysmap.crux.core.client.screen.Screen::invokeController(Ljava/lang/String;Ljava/lang/String;)(call, serializedData);
			return a?a:null;
		};
	}-*/;

	@SuppressWarnings("unused") // called by native code
	private String invokeController(String call, String serializedData)
	{
		Event event = Events.getEvent("_onInvokeController", call);
		InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
		
		if (serializedData != null)
		{
			try
			{
				controllerEvent.setParameter(serializer.deserialize(serializedData));
			}
			catch (ModuleComunicationException e)
			{
				Crux.getErrorHandler().handleError(e.getLocalizedMessage(), e);
				return null;
			}
		}
		
		Object result = Events.callEvent(event, controllerEvent, true);
		try
		{
			return serializer.serialize(result); 
		}
		catch (ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e.getLocalizedMessage(),e);
			return null;
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
	 * 
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	public static <T extends Widget> T get(String id, Class<T> clazz)
	{
		return Screen.get().getWidget(id, clazz);
	}
	
	/**
	 * 
	 * @param id
	 * @param widget
	 */
	public static void add(String id, Widget widget)
	{
		Screen.get().addWidget(id, widget);
	}

	/**
	 * 
	 * @return
	 */
	public static Iterator<String> iterateWidgetsIds()
	{
		return Screen.get().iteratorWidgetsIds();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Iterator<Widget> iterateWidgets()
	{
		return Screen.get().iteratorWidgets();
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
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * 
	 * @param caller
	 */
	public static void updateScreen(Object eventHandler)
	{
		Screen.get().updateScreenWidgets(eventHandler);
	}
	
	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * 
	 * @param caller
	 */
	public static void updateController(Object eventHandler)
	{
		Screen.get().updateControllerObjects(eventHandler);
	}

	/**
	 * 
	 */
	public static void blockToUser()
	{
		Screen.get().showBlockDiv("crux-DefaultScreenBlocker");
	}
	
	/**
	 * 
	 */
	public static void blockToUser(String blockingDivStyleName)
	{
		Screen.get().showBlockDiv(blockingDivStyleName);
	}
	
	/**
	 * 
	 */
	public static void unblockToUser()
	{
		Screen.get().hideBlockDiv();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getId() 
	{
		return Screen.get().getIdentifier();
	}

	/**
	 * 
	 * @return the locale specified or null
	 */
	public static String getLocale()
	{
		String locale = Window.Location.getParameter("locale");
		if (locale == null || locale.length() == 0)
		{
			NodeList<Element> metas = Document.get().getElementsByTagName("meta");
			if (metas != null)
			{
				for (int i=0; i< metas.getLength(); i++)
				{
					MetaElement meta = MetaElement.as(metas.getItem(i));
					if ("gwt:property".equals(meta.getName()) && meta.getContent() != null && meta.getContent().startsWith("locale="))
					{
						locale = meta.getContent().replace("locale=", "").trim();
						break;
					}
				}
			}
		}
		else
		{
			locale = locale.trim();
		}
		if ("".equals(locale))
		{
			locale = null;
		}
		
		return locale;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ModuleComunicationSerializer getCruxSerializer()
	{
		return Screen.get().serializer;
	}
	
	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public static Formatter getFormatter(String formatter)
	{
		return ScreenFactory.getInstance().getClientFormatter(formatter);
	}
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	public static DataSource<?> getDataSource(String dataSource)
	{
		return ScreenFactory.getInstance().getDataSource(dataSource);
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addClosingHandler(ClosingHandler handler) 
	{
		return Screen.get().addWindowClosingHandler(handler);
	}	
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addCloseHandler(CloseHandler<Window> handler) 
	{
		return Screen.get().addWindowCloseHandler(handler);
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addResizeHandler(ResizeHandler handler) 
	{
		return Screen.get().addWindowResizeHandler(handler);
	}		
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addHistoryChangedHandler(ValueChangeHandler<String> handler) 
	{
		return Screen.get().addWindowHistoryChangedHandler(handler);
	}		
	
	/**
	 * 
	 * @param token
	 */
	public static void addToHistory(String token)
	{
		Screen.get().addTokenToHistory(token);
	}

	/**
	 * 
	 * @param token
	 * @param issueEvent
	 */
	public static void addToHistory(String token, boolean issueEvent)
	{
		Screen.get().addTokenToHistory(token, issueEvent);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCurrentHistoryItem()
	{
		return Screen.get().getCurrentHistoryToken();
	}
	
	/**
	 * 
	 */
	public static void createContext()
	{
		ContextManager.createContext();
	}
	
	/**
     *
     */
	public static void clearContext()
	{
		ContextManager.clearContext();
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnTop(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnAbsoluteTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnAbsoluteTop(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnAbsoluteTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callAbsoluteTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * 
	 * @param call
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnOpener(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnOpener(call, param, Object.class);
	}

	/**
	 * 
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnOpener(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callOpenerControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnParent(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnParent(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnParent(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callParentControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnFrame(String frame, String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnFrame(frame, call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnFrame(String frame, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callFrameControllerAccessor(frame, call, Screen.get().serializer.serialize(param)));
	}
	
	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	public static void invokeControllerOnSelf(String call, Object param)
	{
		invokeControllerOnSelf(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnSelf(String call, Object param, Class<T> resultType)
	{
		try
		{
			Event event = Events.getEvent("_onInvokeController", call);
			InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
			controllerEvent.setParameter(param);
			Object result = Events.callEvent(event, controllerEvent, false);
			return (T) result; 
		}
		catch (RuntimeException e)
		{
			Crux.getErrorHandler().handleError(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private static native String callAbsoluteTopControllerAccessor(String call, String serializedData)/*-{
		var who = $wnd.top;
		var op = $wnd.opener;
		while (op != null)
		{
			who = op.top;
			op = op.opener;
		}
		return who._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private static native String callTopControllerAccessor(String call, String serializedData)/*-{
		return $wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private static native String callOpenerControllerAccessor(String call, String serializedData)/*-{
		return $wnd.opener._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private static native String callParentControllerAccessor(String call, String serializedData)/*-{
		return $wnd.parent._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * 
	 * @param frame
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private static native String callFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;		
	
}
