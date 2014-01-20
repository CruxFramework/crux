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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.widgets.ScreenBlocker;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstraction for the entire page. It references the root view and declare methods to 
 * act on the entire application page.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Screen
{
	private static final String CRUX_VIEW_TEST_PAGE = "__CRUX_VIEW_TEST_PAGE__";
	private static Logger logger = Logger.getLogger(Screen.class.getName());
	
	protected RootViewContainer rootViewContainer = null;
	protected FastList<Element> blockingDivs = new FastList<Element>();
	protected String id;
	protected URLRewriter urlRewriter = GWT.create(URLRewriter.class);
	private static HandlerRegistration refreshPreviewHandler;

    protected Screen(final String id) 
	{
		this.id = id;
		rootViewContainer = new RootViewContainer();
		createRootView(id);
	}

	/**
	 * 
	 * @param id
	 */
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
				rootViewContainer.loadView(viewId, true);
			}
		});
    }
	
	/**
	 * 
	 * @param token
	 */
	protected void addTokenToHistory(String token)
	{
		View.addToHistory(token);
	}

	/**
	 * 
	 * @param token
	 * @param issueEvent
	 */
	protected void addTokenToHistory(String token, boolean issueEvent)
	{
		View.addToHistory(token, issueEvent);
	}
	
	/**
	 * 
	 * @param id
	 * @param widget
	 */
	protected void addWidget(String id, Widget widget)
	{
		getView().addWidget(id, widget);
	}

	/**
	 * 
	 * @return
	 */
	protected View getView()
    {
	    return rootViewContainer.getView();
    }

	/**
	 * 
	 * @param id
	 * @param widget
	 */
	protected void addWidget(String id, IsWidget widget)
	{
		getView().addWidget(id, widget.asWidget());
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowCloseHandler(CloseHandler<Window> handler) 
	{
		return getView().addWindowCloseHandler(handler);
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowClosingHandler(ClosingHandler handler) 
	{
		return getView().addWindowClosingHandler(handler);
	}	
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowHistoryChangedHandler(ValueChangeHandler<String> handler) 
	{
		return getView().addWindowHistoryChangedHandler(handler);
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowResizeHandler(ResizeHandler handler) 
	{
		return getView().addResizeHandler(handler);
	}	
	
	/**
	 * 
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowOrientationChangeHandler(OrientationChangeHandler handler)
	{
		return getView().addWindowOrientationChangeHandler(handler);
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
	 * @return
	 */
	protected String getIdentifier() 
	{
		return id;
	}


	/**
	 * Retrieve a widget contained on this screen.
	 * 
	 * @param id
	 * @return
	 */
	protected Widget getWidget(String id)
	{
		return getView().getWidget(id);
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

	protected void removeWidget(String id)
	{
		getView().removeWidget(id);
	}
	
	protected void removeWidget(String id, boolean removeFromDOM)
	{
		getView().removeWidget(id, removeFromDOM);
	}

	protected boolean containsWidget(String id)
	{
		return getView().containsWidget(id);
	}
	
	protected String rewriteURL(String url)
	{
		return urlRewriter.rewrite(url);
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
		ScreenBlocker screenBlocker = new ScreenBlocker(blockingDivStyleName); 
		blockingDivs.add(screenBlocker.getElement());
		body.appendChild(screenBlocker.getElement());
	}
	
	/**
	 * @return
	 */
	protected FastList<String> widgetsIdList()
	{
		return getView().widgetsIdList();
	}		
	
	/**
	 * @return
	 */
	protected FastList<Widget> widgetsList()
	{
		return getView().widgetsList();
	}

	/**
	 * 
	 * @return
	 */
	public static View getRootView()
	{
		return Screen.get().getView();
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
	public static HandlerRegistration addOrientationChangeHandler(final OrientationChangeHandler handler) 
	{
		return Screen.get().addWindowOrientationChangeHandler(handler);
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
	 * @param id
	 * @return
	 */
	public static boolean contains(String id)
	{
		return Screen.get().containsWidget(id);
	}
	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	public static DataSource<?> createDataSource(String dataSource)
	{
		return get().rootViewContainer.getView().createDataSource(dataSource);
	}

	/**
	 * 
	 * @return
	 */
	public RegisteredControllers getRegisteredControllers()
    {
	    return getView().getRegisteredControllers();
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
	 * 
	 * @return
	 */
	public static String getCurrentHistoryItem()
	{
		return Screen.get().getCurrentHistoryToken();
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
	 * Configure a viewport using given contents for small and large displays
	 * @param smallDisplayContent
	 * @param largeDisplayContent
	 */
	public static void configureViewport(String smallDisplayContent, String largeDisplayContent)
	{
		DisplayHandler.configureViewport(smallDisplayContent, largeDisplayContent);
	}
	
	/**
	 * Configure a viewport using given content
	 * @param content
	 */
	public static void configureViewport(String content)
	{
		DisplayHandler.configureViewport(content);
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
	 * 
	 * @return
	 */
	public static boolean isIos()
	{
		String userAgent = Window.Navigator.getUserAgent().toLowerCase();
		return (userAgent.indexOf("iphone") > 0 || userAgent.indexOf("ipod") > 0 || userAgent.indexOf("ipad") > 0);
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isAndroid()
	{
		String userAgent = Window.Navigator.getUserAgent().toLowerCase();
		return (userAgent.indexOf("android") > 0);
	}
	
    public static native void reload() /*-{
    	$wnd.top.location.reload();
  	}-*/;
    
	/**
	 * Enable or disable the browser refresh support.
	 * @param enabled
	 */
    public static void setRefreshEnabled(boolean enabled)
    {
    	if (enabled)
    	{
    		if (refreshPreviewHandler != null)
    		{
    			refreshPreviewHandler.removeHandler();
    			refreshPreviewHandler = null;
    		}
    	}
    	else
    	{
    		disableRefresh();
    	}
    }
    
    private static void disableRefresh()
	{
		if (refreshPreviewHandler == null)
		{
			refreshPreviewHandler = com.google.gwt.user.client.Event.addNativePreviewHandler(new NativePreviewHandler(){
				public void onPreviewNativeEvent(NativePreviewEvent event)
				{
					if (event.getTypeInt() == com.google.gwt.user.client.Event.ONKEYDOWN)
					{
						NativeEvent nEvent = event.getNativeEvent();
						if (nEvent.getCtrlKey() && nEvent.getKeyCode() == 'R')
						{
							nEvent.preventDefault();
						}
						
						if (nEvent.getKeyCode() == 116)//F5
						{
							nEvent.preventDefault();
						}
					}
				}
			});
		}
    }
}
