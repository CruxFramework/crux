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
package br.com.sysmap.crux.advanced.client.dialog;

import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseHandler;
import br.com.sysmap.crux.advanced.client.event.openclose.HasBeforeCloseHandlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Popup extends Widget implements HasBeforeCloseHandlers, HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "crux-Popup" ;
	private static CruxInternalPopupController popupController = null;
	private String title;
	private String url;
	private String styleName;
	private String width = "400";
	private String height = "300";
	private boolean animationEnabled;
	private boolean closeable = true;
	protected static Popup popup;
	
	/**
	 * 
	 */
	public Popup()
	{
		setElement(DOM.createSpan());
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}
	
	public boolean isCloseable()
	{
		return closeable;
	}

	public void setCloseable(boolean closeable)
	{
		this.closeable = closeable;
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
		popup = this;
		popupController.showPopup(new PopupData(title, url, width, height, styleName!=null ? styleName : DEFAULT_STYLE_NAME, animationEnabled, closeable));
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
	 * 
	 * @param title
	 * @param url
	 * @param beforeCloseHandler
	 * @param styleName
	 * @param animationEnabled
	 */
	public static void show(String title, String url, String width, String height, BeforeCloseHandler beforeCloseHandler, String styleName, boolean animationEnabled, boolean closeable)
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
		
		popup.show();
	}
	
	public static void close()
	{
		CruxInternalPopupController.hide();
	}

	/**
	 * @return the width
	 */
	public String getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width)
	{
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public String getHeight()
	{
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height)
	{
		this.height = height;
	}

	/*
	 * @see br.com.sysmap.crux.advanced.client.event.openclose.HasBeforeCloseHandlers#addBeforeCloseHandler(br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseHandler)
	 */
	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
	{
		return addHandler(handler, BeforeCloseEvent.getType());
	}
}
