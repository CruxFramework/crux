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
package br.com.sysmap.crux.widgets.client.dialog;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseHandler;
import br.com.sysmap.crux.widgets.client.event.openclose.HasBeforeCloseHandlers;
import br.com.sysmap.crux.widgets.client.event.openclose.HasOpenHandlers;
import br.com.sysmap.crux.widgets.client.event.openclose.OpenEvent;
import br.com.sysmap.crux.widgets.client.event.openclose.OpenHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Popup extends Widget implements HasBeforeCloseHandlers, HasAnimation, HasOpenHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-Popup" ;
	protected static FastList<Popup> popups = new FastList<Popup>();
	private static CruxInternalPopupController popupController = null;
	private boolean animationEnabled;
	private boolean closeable = true;
	private String height = "300";
	private String styleName;
	private String title;
	private String url;
	private String width = "400";
	
	/**
	 * 
	 */
	public Popup()
	{
		setElement(DOM.createSpan());
	}

	/**
	 * 
	 */
	public static void close()
	{
		CruxInternalPopupController.hide();
	}

	/**
	 * @param fireCloseEvents
	 */
	public static void close(boolean fireCloseEvents)
	{
		CruxInternalPopupController.hide(fireCloseEvents);
	}
	
	/**
	 * @return
	 */
	public static Popup getLastShownPopup()
	{
		if(popups.size() > 0)
		{
			return popups.get(popups.size() - 1);
		}
		
		return null;
	}

	/**
	 * @return the window object of the popup opener
	 */
	public static JSWindow getOpener()
	{
		return CruxInternalPopupController.getOpener();
	}
	
	/**
	 * @return the window object of the popup opener
	 */
	public static JSWindow getWindow()
	{
		return CruxInternalPopupController.getWindow();
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnOpener(String call, Object param) throws br.com.sysmap.crux.core.client.screen.ModuleComunicationException
	{
		CruxInternalPopupController.invokeOnOpener(call, param);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static <T> T invokeOnOpener(String call, Object param, Class<T> resultType) throws br.com.sysmap.crux.core.client.screen.ModuleComunicationException
	{
		return CruxInternalPopupController.invokeOnOpener(call, param, resultType);
	}

	/**
	 * 
	 * @param title
	 * @param url
	 * @param beforeCloseHandler
	 */
	public static void show(String title, String url,  BeforeCloseHandler beforeCloseHandler)
	{
		show(title, url, null, null, beforeCloseHandler, DEFAULT_STYLE_NAME, false, true);
	}
	
	/**
	 * @param title
	 * @param url
	 * @param width
	 * @param height
	 * @param beforeCloseHandler
	 * @param styleName
	 * @param animationEnabled
	 * @param closeable
	 */
	public static void show(String title, String url, String width, String height, BeforeCloseHandler beforeCloseHandler, String styleName, boolean animationEnabled, boolean closeable)
	{
		fullShow(title, url, width, height, beforeCloseHandler, null, styleName, animationEnabled, closeable);
	}
	
	/**
	 * @param title
	 * @param url
	 * @param width
	 * @param height
	 * @param beforeCloseHandler
	 * @param openHandler
	 * @param styleName
	 * @param animationEnabled
	 * @param closeable
	 */
	public static void show(String title, String url, String width, String height, BeforeCloseHandler beforeCloseHandler, OpenHandler openHandler, String styleName, boolean animationEnabled, boolean closeable)
	{
		fullShow(title, url, width, height, beforeCloseHandler, openHandler, styleName, animationEnabled, closeable);
	}
	
	/**
	 * Full parameterized internal method
	 * @param title
	 * @param url
	 * @param width
	 * @param height
	 * @param beforeCloseHandler
	 * @param openHandler
	 * @param styleName
	 * @param animationEnabled
	 * @param closeable
	 */
	private static void fullShow(String title, String url, String width, String height, BeforeCloseHandler beforeCloseHandler, OpenHandler openHandler, String styleName, boolean animationEnabled, boolean closeable)
	{
		Popup popup = new Popup(); 
		popup.setTitle(title);
		popup.setUrl(url);
		popup.setStyleName(styleName);
		popup.setCloseable(closeable);
		
		if(width != null)
		{
			popup.setWidth(width);
		}
		if(height != null)
		{
			popup.setHeight(height);
		}			
		
		popup.setAnimationEnabled(animationEnabled);
		
		if (beforeCloseHandler != null)
		{
			popup.addBeforeCloseHandler(beforeCloseHandler);
		}
		
		if (openHandler != null)
		{
			popup.addOpenHandler(openHandler);
		}		
		
		popup.show();
	}

	public static void unregisterLastShownPopup()
	{
		if(popups.size() > 0)
		{
			popups.remove(popups.size() - 1);
		}
	}
	
	/*
	 * @see br.com.sysmap.crux.widgets.client.event.openclose.HasBeforeCloseHandlers#addBeforeCloseHandler(br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseHandler)
	 */
	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
	{
		return addHandler(handler, BeforeCloseEvent.getType());
	}

	/**
	 * @return the height
	 */
	public String getHeight()
	{
		return height;
	}

	public String getStyleName()
	{
		return styleName;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * @return the width
	 */
	public String getWidth()
	{
		return width;
	}
	
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}
	
	public boolean isCloseable()
	{
		return closeable;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}
	
	public void setCloseable(boolean closeable)
	{
		this.closeable = closeable;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height)
	{
		this.height = height;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width)
	{
		this.width = width;
	}

	public HandlerRegistration addOpenHandler(OpenHandler handler)
	{
		return addHandler(handler, OpenEvent.getType());
	}

	/**
	 * 
	 */
	public void show()
	{
		if (popupController == null)
		{
			popupController = new CruxInternalPopupController(); 
		}
		popups.add(this);
		
		Timer timer = new Timer()
		{
			@Override
			public void run()
			{
				popupController.showPopup(new PopupData(title, url, width, height, styleName!=null ? styleName : DEFAULT_STYLE_NAME, animationEnabled, closeable));
			}
		};
		
		timer.schedule(10);
	}
}
