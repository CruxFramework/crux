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
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.context.ContextManager;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.event.Event;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
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
@Legacy(value=Screen.class)
public class ScreenLegacy
{

	/**
	 * 
	 * @param id
	 */
    @SuppressWarnings("deprecation")
	protected void createRootView(String id)
    {
		if (!GWT.isScript())
		{
			if (id.endsWith("/"+CRUX_VIEW_TEST_PAGE))
			{
				id = Window.Location.getParameter("viewName");
				assert(id != null) : "To use viewTester, your must inform the viewName parameter.";
			}
		}
	    final String viewId = id;
	    Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				ViewContainer.createView(viewId, new CreateCallback()
				{
					@Override
					public void onViewCreated(View view)
					{
						if (!rootViewContainer.add(view, true, null))
						{
							Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
						}
					    if (Crux.getConfig().enableCrux2OldInterfacesCompatibility())
					    {
					    	addWindowCloseHandler(new CloseHandler<Window>()
					    	{
					    		public void onClose(CloseEvent<Window> event)
					    		{
					    			removeControllerAccessor(Screen.this);
					    		}
					    	});
					    }

					    addWindowCloseHandler(new CloseHandler<Window>()
					    {
					    	public void onClose(CloseEvent<Window> event)
					    	{
					    		removeCrossDocumentAccessor(Screen.this);
					    	}
					    });
					}
				});
			}
		});

	    createCrossDocumentAccessor(Screen.this);
	    if (Crux.getConfig().enableCrux2OldInterfacesCompatibility())
	    {
	    	this.serializer = new ModuleComunicationSerializer();
	    	createControllerAccessor(Screen.this);
	    }
    }
	
	@Deprecated
	@Legacy
	protected ModuleComunicationSerializer serializer = null;
	
	
	@Deprecated
	@Legacy
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
	@Deprecated
	@Legacy
	native static void createCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = function(serializedData){
			var a = handler.@org.cruxframework.crux.core.client.screen.Screen::invokeCrossDocument(Ljava/lang/String;)(serializedData);
			return a?a:null;
		};
	}-*/;

	@Deprecated
	@Legacy
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
	@Deprecated
	@Legacy
	private String invokeCrossDocument(String serializedData)
	{
		return getRegisteredControllers().invokeCrossDocument(serializedData);
	}
	
	/**
	 * @return
	 * @deprecated Use widgetsList() instead
	 */
	@Deprecated
	@Legacy
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
	@Legacy
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

	@Deprecated
	@Legacy
	private native void removeControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = null;
	}-*/;

	/**
	 * Remove the cross document hook function
	 * @param handler
	 */
	@Deprecated
	@Legacy
	private native void removeCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = null;
	}-*/;
	
	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * @param caller
	 */
	@Deprecated
	@Legacy
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
	@Deprecated
	@Legacy
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
	 * 
	 * @param url
	 */
	@Deprecated
	@Legacy
	public static String appendDebugParameters(String url)
	{
		return rewriteUrl(url);
	}

	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	@Legacy
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
	@Legacy
	private static native String callFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	@Legacy
	private static native String callOpenerControllerAccessor(String call, String serializedData)/*-{
		return $wnd.opener._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	@Legacy
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
	@Legacy
	private static native String callSiblingFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.parent.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	@Legacy
	private static native String callTopControllerAccessor(String call, String serializedData)/*-{
		return $wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @return a list containing all widgets from the current screen 
	 * @deprecated Use listWidgets() instead
	 */
	@Deprecated
	@Legacy
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
	@Legacy
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
	@Legacy
	public static ModuleComunicationSerializer getCruxSerializer()
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		return Screen.get().serializer;
	}
	
	/**
	 * @deprecated Use createDataSource(java.lang.String) instead
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	@Legacy
	public static DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	@Deprecated
	@Legacy
	public static Formatter getFormatter(String formatter)
	{
		return ScreenFactory.getInstance().getClientFormatter(formatter);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
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
	@Legacy
	public static Iterator<Widget> iterateWidgets()
	{
		return Screen.get().iteratorWidgets();
	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	@Legacy
	public static Iterator<String> iterateWidgetsIds()
	{
		return Screen.get().iteratorWidgetsIds();
	}

	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * 
	 * @param caller
	 */
	@Deprecated
	@Legacy
	public static void updateController(Object eventHandler)
	{
		Screen.get().updateControllerObjects(eventHandler);
	}
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * 
	 * @param caller
	 */
	@Deprecated
	@Legacy
	public static void updateScreen(Object eventHandler)
	{
		Screen.get().updateScreenWidgets(eventHandler);
	}
	

	/**
    *
    */
	@Legacy
	@Deprecated
	public static void clearContext()
	{
		ContextManager.clearContext();
	}

	/**
	 * 
	 */
	@Legacy
	@Deprecated
	public static void createContext()
	{
		ContextManager.createContext();
	}

	
}
