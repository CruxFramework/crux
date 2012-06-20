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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.context.ContextManager;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.event.Event;
import org.cruxframework.crux.core.client.executor.BeginEndExecutor;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstraction for the entire page. It contains all widgets, containers and 
 * datasources.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Screen
{
	private static Logger logger = Logger.getLogger(Screen.class.getName());
	
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
	 * @param id
	 * @param widget
	 */
	public static void add(String id, IsWidget widget)
	{
		Screen.get().addWidget(id, widget);
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
	public static HandlerRegistration addClosingHandler(ClosingHandler handler) 
	{
		return Screen.get().addWindowClosingHandler(handler);
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
	public static HandlerRegistration addOrientationChangeOrResizeHandler(final OrientationChangeOrResizeHandler handler) 
	{
		return Screen.get().addWindowOrientationChangeOrResizeHandler(handler);
	}
	
	/**
	 * 
	 * @return
	 */
	public static double getScreenZoomFactorForCurrentDevice()
	{
		return DeviceDisplayHandler.getScreenZoomFactor();
	}
	
	/**
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowOrientationChangeOrResizeHandler(final OrientationChangeOrResizeHandler handler) 
	{
		final BeginEndExecutor executor = new BeginEndExecutor(100) 
		{
			private int clientHeight = Window.getClientHeight();
			private int clientWidth = Window.getClientWidth();

			@Override
			protected void doEndAction() 
			{
				if (!getCurrentDevice().equals(Device.largeDisplayMouse))
				{
					int newClientHeight = Window.getClientHeight();
					int newClientWidth = Window.getClientWidth();
					if (this.clientHeight != newClientHeight && clientWidth != newClientWidth)
					{
						handler.onOrientationChangeOrResize();
					}
					clientHeight = newClientHeight;
					clientWidth  = newClientWidth;
				}
				else
				{
					handler.onOrientationChangeOrResize();
				}
			}
			
			@Override
			protected void doBeginAction() 
			{
				// nothing
			}
		};
		
		ResizeHandler resizeHandler = new ResizeHandler() 
		{
			public void onResize(ResizeEvent event) 
			{
				executor.execute();
			}
		};
		
		final HandlerRegistration resizeHandlerRegistration = addResizeHandler(resizeHandler);
		final JavaScriptObject orientationHandler = attachOrientationChangeHandler(executor);
		
		return new HandlerRegistration() 
		{
			public void removeHandler() 
			{
				resizeHandlerRegistration.removeHandler();
				
				if(orientationHandler != null)
				{
					removeOrientationChangeHandler(orientationHandler);
				}
			}
		};
	}
	
	/**
	 * @param orientationHandler
	 */
	private native void removeOrientationChangeHandler(JavaScriptObject orientationHandler) /*-{

		var supportsOrientationChange = 'onorientationchange' in $wnd;
		
		if (supportsOrientationChange)
		{
			$wnd.removeEventListener("orientationchange", orientationHandler);
		}
		
	}-*/;
	
	/**
	 * @param executor
	 * @return
	 */
	private native JavaScriptObject attachOrientationChangeHandler(BeginEndExecutor executor)/*-{
	
		var supportsOrientationChange = 'onorientationchange' in $wnd;
		
		if (supportsOrientationChange)
		{	
			$wnd.previousOrientation = $wnd.orientation;
			var checkOrientation = function()
			{
			    if($wnd.orientation !== $wnd.previousOrientation)
			    {
			        $wnd.previousOrientation = $wnd.orientation;
		        	executor.@org.cruxframework.crux.core.client.executor.BeginEndExecutor::execute()();
			    }
			};
		
			$wnd.addEventListener("orientationchange", checkOrientation, false);
			
			return checkOrientation;
		}
		
		return null;
	}-*/;
	
	
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
	 * @param url
	 */
	@Deprecated
	public static String appendDebugParameters(String url)
	{
		return rewriteUrl(url);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String rewriteUrl(String url)
	{
		try
		{
			return Screen.get().rewriteURL(url);
		}
		catch(Throwable e)
		{
			logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return url;
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
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
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
	 * 
	 * @param frame
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callOpenerControllerAccessor(String call, String serializedData)/*-{
		return $wnd.opener._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
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
	@Deprecated
	private static native String callSiblingFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.parent.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callTopControllerAccessor(String call, String serializedData)/*-{
		return $wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
     *
     */
	public static void clearContext()
	{
		ContextManager.clearContext();
	}

	/**
	 * @param id
	 * @return
	 */
	public static boolean contains(String id)
	{
		return Screen.get().containsWidget(id);
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
	 * @param dataSource
	 * @return
	 */
	public static DataSource<?> createDataSource(String dataSource)
	{
		return ScreenFactory.getInstance().createDataSource(dataSource);
	}
	
	/**
	 * If the given widget does not have a non-empty ID attribute, sets the given id into it. 
	 * @param widget
	 * @param id
	 */
	public static void ensureDebugId(Widget widget, String id)
	{
		if(widget != null)
		{
			if(StringUtils.isEmpty(widget.getElement().getId()))
			{
				id = id.replaceAll("[^a-zA-Z0-9\\$]", "_");
				id = id.length() > 100 ? id.substring(0, 100) : id;
				
				if(!id.startsWith("_"))
				{
					id = "_" + id;
				}
				
				id = id.toLowerCase();
				
				id = ensureUniqueId(id);
				
				widget.getElement().setId(id);
			}
		}
	}

	/**
	 * Ensures that the given id is not being used in the current document. If this is not the case, returns an unique id.
	 * @param id
	 * @return
	 */
	private static String ensureUniqueId(String id)
	{
		Object exists = DOM.getElementById(id);
		int i = 0;
		while(exists != null)
		{
			exists = DOM.getElementById(id + "_" + (++i));
		}
		if(i > 0)
		{
			id = id + "_" + i;
		}
		return id;
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
	public static <T extends IsWidget> T get(String id, Class<T> clazz)
	{
		return Screen.get().getWidget(id, clazz);
	}
	
	/**
	 * @return a list containing all widgets from the current screen 
	 * @deprecated Use listWidgets() instead
	 */
	@Deprecated
	public static List<Widget> getAllWidgets()
	{
		FastList<Widget> keys = listWidgets();
		List<Widget> values = new ArrayList<Widget>(keys.size());		
		for (int i=0; i<keys.size(); i++)
		{
			values.add(keys.get(i));
		}		
		return values;
	}

	/**
	 * @return a list containing all widgets ids from the current screen 
	 * @deprecated Use listWidgetIds() instead
	 */
	@Deprecated
	public static List<String> getAllWidgetsIds()
	{
		FastList<String> keys = listWidgetIds();
		List<String> ids = new ArrayList<String>(keys.size());		
		for (int i=0; i<keys.size(); i++)
		{
			ids.add(keys.get(i));
		}		
		return ids;
	}
	
	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static ModuleComunicationSerializer getCruxSerializer()
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return Screen.get().serializer;
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
	 * @deprecated Use createDataSource(java.lang.String) instead
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	public static DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
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
	 * @return
	 */
	public static Device getCurrentDevice()
	{
		return ScreenFactory.getInstance().getCurrentDevice();
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
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		
		if ("".equals(locale))
		{
			locale = null;
		}
		
		return locale;
	}	
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnAbsoluteTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnAbsoluteTop(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnAbsoluteTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callAbsoluteTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}
	
	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnFrame(String frame, String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnFrame(frame, call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnFrame(String frame, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callFrameControllerAccessor(frame, call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * 
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
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
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnOpener(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callOpenerControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnParent(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnParent(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnParent(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callParentControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnSelf(String call, Object param)
	{
		invokeControllerOnSelf(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnSelf(String call, Object param, Class<T> resultType)
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		try
		{
			Event event = org.cruxframework.crux.core.client.event.Events.getEvent("_onInvokeController", call);
			InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
			controllerEvent.setParameter(param);
			Object result = org.cruxframework.crux.core.client.event.Events.callEvent(event, controllerEvent, false);
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
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnSiblingFrame(String frame, String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnSiblingFrame(frame, call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnSiblingFrame(String frame, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callSiblingFrameControllerAccessor(frame, call, Screen.get().serializer.serialize(param)));
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnTop(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return (T) Screen.get().serializer.deserialize(callTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static Iterator<Widget> iterateWidgets()
	{
		return Screen.get().iteratorWidgets();
	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static Iterator<String> iterateWidgetsIds()
	{
		return Screen.get().iteratorWidgetsIds();
	}
	
	/**
	 * @return
	 */
	public static FastList<String> listWidgetIds()
	{
		return Screen.get().widgetsIdList();
	}
	
	/**
	 * @return
	 */
	public static FastList<Widget> listWidgets()
	{
		return Screen.get().widgetsList();
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
	 * 
	 */
	public static void unblockToUser()
	{
		Screen.get().hideBlockDiv();
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
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * 
	 * @param caller
	 */
	public static void updateScreen(Object eventHandler)
	{
		Screen.get().updateScreenWidgets(eventHandler);
	}
	
	protected FastList<Element> blockingDivs = new FastList<Element>();

	@Deprecated
	protected String[] declaredControllers;
	
	@Deprecated
	protected String[] declaredDataSources;

	@Deprecated
	protected String[] declaredFormatters;
	
	@Deprecated
	protected String[] declaredSerializables;
	
	protected IFrameElement historyFrame = null;
	
	protected String id;

	protected Map<String> lazyWidgets = null;
	
	protected ScreenBlocker screenBlocker = GWT.create(ScreenBlocker.class);
	
	protected URLRewriter urlRewriter = GWT.create(URLRewriter.class);
	
	@Deprecated
	protected ModuleComunicationSerializer serializer = null;

	protected FastMap<Widget> widgets = new FastMap<Widget>();
	
	@SuppressWarnings("deprecation")
    protected Screen(String id, Map<String> lazyWidgets) 
	{
		this.id = id;
		this.lazyWidgets = lazyWidgets;
		if (LogConfiguration.loggingIsEnabled())
		{
			Array<String> keys = lazyWidgets.keys();
			for(int i=0; i< keys.size(); i++)
			{
				logger.log(Level.FINE, "Adding lazy dependency. Widget["+keys.get(i)+"] depends on ["+lazyWidgets.get(keys.get(i))+"].");
			}
		}
		if (Crux.getConfig().enableCrux2OldInterfacesCompatibility())
		{
			this.serializer = new ModuleComunicationSerializer();
			createControllerAccessor(this);
			this.addWindowCloseHandler(new CloseHandler<Window>()
			{
				public void onClose(CloseEvent<Window> event)
				{
					removeControllerAccessor(Screen.this);
				}
			});
		}

		this.addWindowCloseHandler(new CloseHandler<Window>()
		{
			public void onClose(CloseEvent<Window> event)
			{
				removeCrossDocumentAccessor(Screen.this);
			}
		});
	}
	
	/**
	 * 
	 * @param token
	 */
	protected void addTokenToHistory(String token)
	{
		History.newItem(token, false);
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
	
	protected void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}

	protected void addWidget(String id, IsWidget widget)
	{
		widgets.put(id, widget.asWidget());
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
	protected HandlerRegistration addWindowClosingHandler(ClosingHandler handler) 
	{
		return Window.addWindowClosingHandler(handler);
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
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowResizeHandler(ResizeHandler handler) 
	{
		return Window.addResizeHandler(handler);
	}	
	
	/**
	 * @param widgetId
	 */
	protected void cleanLazyDependentWidgets(String widgetId)
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Cleaning lazy dependencies of lazyPanel ["+widgetId+"]...");
		}
		FastList<String> dependentWidgets = getDependentWidgets(widgetId);
		for (int i=0; i<dependentWidgets.size(); i++)
		{
			lazyWidgets.remove(dependentWidgets.get(i));
		}
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Lazy dependencies of lazyPanel ["+widgetId+"] removed.");
		}
	}

	/**
	 * @param id
	 * @return
	 */
	protected boolean containsWidget(String id)
	{
		return widgets.containsKey(id);
	}		
	
	@Deprecated
	private native void createControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = function(call, serializedData){
			var a = handler.@org.cruxframework.crux.core.client.screen.Screen::invokeController(Ljava/lang/String;Ljava/lang/String;)(call, serializedData);
			return a?a:null;
		};
	}-*/;		
	
	/**
	 * Create a hook javascript function, called outside of module.
	 * @param handler
	 */
	native static void createCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = function(serializedData){
			var a = handler.@org.cruxframework.crux.core.client.screen.Screen::invokeCrossDocument(Ljava/lang/String;)(serializedData);
			return a?a:null;
		};
	}-*/;

	/**
	 * 
	 * @return
	 */
	protected String getCurrentHistoryToken()
	{
		return History.getToken();
	}
	
	/**
	 * @param widgetId
	 * @return
	 */
	private FastList<String> getDependentWidgets(String widgetId)
	{
		FastList<String> dependentWidgets = new FastList<String>();
		Array<String> keys = lazyWidgets.keys();
		int size = keys.size();
		for (int i=0; i<size; i++)
		{
			String key = keys.get(i);
			if (lazyWidgets.get(key).equals(widgetId))
			{
				dependentWidgets.add(key);
			}
		}
		return dependentWidgets;
	}
	
	/**
	 * @return
	 */
	protected String getIdentifier() 
	{
		return id;
	}

	/**
	 * If a lazyPanel contains as child a panel that is not visible, the enclosing
	 * lazy panel of the child is only created when the external lazyPanel ensureWidget 
	 * method is called. It means that a new dependency was created during the initialization
	 * of the first panel. We must check for this situation and add this new dependency here.
	 * 
	 * @param id
	 * @param lazyPanelId
	 * @return
	 */
	private Widget getRuntimeDependencyWidget(String id, String lazyPanelId)
    {
		Widget widget = null;
	    if (ViewFactoryUtils.isWholeWidgetLazyWrapper(lazyPanelId))  
	    {
	    	lazyPanelId = ViewFactoryUtils.getWrappedWidgetIdFromLazyPanel(lazyPanelId); 
	    	lazyPanelId = ViewFactoryUtils.getLazyPanelId(lazyPanelId, LazyPanelWrappingType.wrapChildren);
	    	if (widgets.containsKey(lazyPanelId))  
	    	{
	    		/* When the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapChildren
	    	     lazy instantiation.*/
	    		lazyWidgets.put(id, lazyPanelId);
	    		widget = getWidget(id);
	    	}
	    	else
	    	{
	    		 /* Check if the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	    		  lazy instantiation.*/
		    	widget = getRuntimeDependencyWidgetFromWholeWidget(id);
	    	}
	    }
	    else
	    {
	   		 /* Check if the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	  		  lazy instantiation.*/
	    	widget = getRuntimeDependencyWidgetFromWholeWidget(id);
	    }
	    return widget;
    }
	
	/**
	 * When we have multi-level inner lazy panels, the most inside panel is dependent from the most outside one.
	 * If the most outside is initialized, a new dependency must be created for the inner lazy panels not yet 
	 * initialized.
	 * @param id
	 * @param lazyPanelId
	 */
	void checkRuntimeLazyDependency(String id, String lazyPanelId)
	{
		if (!lazyWidgets.containsKey(id))
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "New runtime lazy dependency found. Widget["+id+"] depends on LazyPanel["+lazyPanelId+"]...");
			}
			lazyWidgets.put(id, lazyPanelId);
		}
	}
	
	/**
	 * When the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	 * lazy instantiation.
	 * 
	 * @param id
	 * @return
	 */
	private Widget getRuntimeDependencyWidgetFromWholeWidget(String id)
    {
		Widget widget = null;
		String lazyPanelId;
	    lazyPanelId = ViewFactoryUtils.getLazyPanelId(id, LazyPanelWrappingType.wrapWholeWidget);
	    if (widgets.containsKey(lazyPanelId))  
	    {
	    	lazyWidgets.put(id, lazyPanelId);
	    	widget = getWidget(id);
	    }
	    return widget;
    }
	
	/**
	 * Retrieve a widget contained on this screen. If the the requested widget does not exists, we check if
	 * a request for a lazy creation of this widget was previously done. If so, we initialize the wrapper 
	 * required panel (according with {@code lazyWidgets} map) and try again.
	 * 
	 * @param id
	 * @return
	 */
	protected Widget getWidget(String id)
	{
		Widget widget = widgets.get(id);
		if (widget == null)
		{
			String lazyPanelId = lazyWidgets.get(id);
			if (lazyPanelId != null)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Found a lazy dependency. Widget["+id+"] depends on ["+lazyPanelId+"].");
				}
				if (initializeLazyDependentWidget(lazyPanelId))
				{
					widget = widgets.get(id);
					if (widget == null)
					{
						/*
						 * If a lazyPanel contains as child a panel that is not visible, the enclosing
						 * lazy panel of the child is only created when the external lazyPanel ensureWidget 
						 * method is called. It means that a new dependency was created during the initialization
						 * of the first panel. We must check for this situation and add this new dependency here.
						 */
						widget = getRuntimeDependencyWidget(id, lazyPanelId);
					}
				}
			}
		}
		return widget;
	}
	
	/**
	 * Retrieve a widget contained on this screen. 
	 * 
	 * @param id
	 * @param checkLazyDependencies
	 * @return
	 */
	protected Widget getWidget(String id, boolean checkLazyDependencies)
	{
		if (checkLazyDependencies)
		{
			return getWidget(id);
		}
		else
		{
			return widgets.get(id);
		}
	}

	/**
	 * Generic version of <code>getWidget</code> method
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends IsWidget> T getWidget(String id, Class<T> clazz)
	{
		Widget w = getWidget(id);
		return (T) w;
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
	 * Call the {@code LazyPanel.ensureWidget()} method of the given lazyPanel.
	 * This method can trigger other dependent lazyPanel initialization, through 
	 * a recursive call to {@code Screen.getWidget(String)}.
	 * 
	 * @param widgetId lazyPanel to be initialized
	 * @return true if some lazyPanel was really initialized for this request
	 */
	protected boolean initializeLazyDependentWidget(String widgetId)
	{
		boolean ret = false;
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Initializing lazy dependents widgets of lazyPanel ["+widgetId+"]...");
		}

		LazyPanel lazyPanel = (LazyPanel) widgets.get(widgetId);
		if (lazyPanel == null)
		{
			if (getWidget(ViewFactoryUtils.getWrappedWidgetIdFromLazyPanel(widgetId)) != null)
			{
				lazyPanel = (LazyPanel) widgets.get(widgetId);
			}
		}
		
		if (lazyPanel != null)
		{
			lazyPanel.ensureWidget();
			ret = true;
		}
		else
		{
			cleanLazyDependentWidgets(widgetId);
		}

		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, " Lazy dependents widgets of lazyPanel ["+widgetId+"] are now initialized.");
		}
		return ret;
	}
	
	@Deprecated
	@SuppressWarnings("unused") // called by native code
	private String invokeController(String call, String serializedData)
	{
		Event event = org.cruxframework.crux.core.client.event.Events.getEvent("_onInvokeController", call);
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
		
		Object result = org.cruxframework.crux.core.client.event.Events.callEvent(event, controllerEvent, true);
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
	 * Make a call to a cross document object.
	 * 
	 * @param serializedData
	 * @return
	 */
	@SuppressWarnings("unused") // called by native code
	private String invokeCrossDocument(String serializedData)
	{
		return ScreenFactory.getInstance().getRegisteredControllers().invokeCrossDocument(serializedData);
	}
	
	/**
	 * @return
	 * @deprecated Use widgetsList() instead
	 */
	@Deprecated
	protected Iterator<Widget> iteratorWidgets()
	{
		FastList<Widget> widgetList = widgetsList();
		ArrayList<Widget> result = new ArrayList<Widget>();
		for (int i=0; i<widgetList.size(); i++)
		{
			result.add(widgetList.get(i));
		}
		
		return result.iterator();
	}

	/**
	 * @return
	 * @deprecated Use widgetsIdList() instead
	 */
	@Deprecated
	protected Iterator<String> iteratorWidgetsIds()
	{
		FastList<String> idList = widgetsIdList();
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0; i<idList.size(); i++)
		{
			result.add(idList.get(i));
		}
		
		return result.iterator();
	}

	/**
	 * 
	 */
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
	
	@Deprecated
	private native void removeControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = null;
	}-*/;

	/**
	 * Remove the cross document hook function
	 * @param handler
	 */
	private native void removeCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = null;
	}-*/;

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
	
	protected String rewriteURL(String url)
	{
		return urlRewriter.rewrite(url);
	}
	
	/**
	 * @param declaredControllers
	 */
	@Deprecated
	void setDeclaredControllers(String[] declaredControllers) {
		this.declaredControllers = declaredControllers;
	}

	/**
	 * @param declaredDataSources
	 */
	@Deprecated
	void setDeclaredDataSources(String[] declaredDataSources) {
		this.declaredDataSources = declaredDataSources;
	}
	
	/**
	 * @param declaredFormatters
	 */
	@Deprecated
	void setDeclaredFormatters(String[] declaredFormatters) {
		this.declaredFormatters = declaredFormatters;
	}

	/**
	 * @param declaredSerializables
	 */
	@Deprecated
	void setDeclaredSerializables(String[] declaredSerializables) {
		this.declaredSerializables = declaredSerializables;
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
	 * @return
	 */
	protected FastList<String> widgetsIdList()
	{
		return widgets.keys();
	}		
	
	/**
	 * @return
	 */
	protected FastList<Widget> widgetsList()
	{
		FastList<String> keys = widgets.keys();
		FastList<Widget> values = new FastList<Widget>();
		for (int i=0; i<keys.size(); i++)
		{
			values.add(widgets.get(keys.get(i)));
		}
		
		return values;
	}
	
	public static interface OrientationChangeOrResizeHandler
	{
		public void onOrientationChangeOrResize();
	}
}
