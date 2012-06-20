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
package org.cruxframework.crux.core.client.controller.crossdevice;

import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DeviceAdaptiveController
{
	private static final String CHILD_ID_PREFIX = "__DEV_ADTV_";
	private static int idCounter = 0;
	
	private final String childPrefixId;
	private Widget boundWidget;
	
	
	/**
	 * 
	 */
	public DeviceAdaptiveController()
    {
		this.childPrefixId = CHILD_ID_PREFIX+(idCounter++);
    }

	/**
	 * Override this method to define default stylename for the widget
	 */
	protected abstract void initWidgetDefaultStyleName();
	
	/**
	 * Override this method to apply dependent stylenames for the widget
	 */
	protected abstract void applyWidgetDependentStyleNames();

	/**
	 * Override this method if you need to add some startup code for component
	 */
	protected void init()
	{
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected <T extends IsWidget> T getChildWidget(String id)
	{
		return (T)Screen.get(getChildWidgetId(id));
	}

	/**
	 * 
	 */
	protected void addWidget(String id, Widget widget)
	{
		Screen.add(getChildWidgetId(id), widget);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getChildWidgetId(String id)
	{
		return childPrefixId+id;
	}
	
	void setBoundWidget(Widget boundWidget)
	{
		this.boundWidget = boundWidget;
	}
	
	public void setWidth(String width)
	{
		boundWidget.setWidth(width);
	}
	
	public void setVisible(boolean visible)
	{
		boundWidget.setVisible(visible);
	}
	
	public boolean isVisible()
	{
		return boundWidget.isVisible();
	}
	
	public void setStyleName(String style)
	{
		boundWidget.setStyleName(style);
		applyWidgetDependentStyleNames();
	}
	
	public String getStyleName()
	{
		return boundWidget.getStyleName();
	}
	
	public void setTitle(String title)
	{
		boundWidget.setTitle(title);
	}
	
	public String getTitle()
	{
		return boundWidget.getTitle();
	}
	
	public void setHeight(String height)
	{
		boundWidget.setHeight(height);
	}
		
	public com.google.gwt.user.client.Element getElement()
	{
		return boundWidget.getElement();
	}
	
	public HandlerRegistration addAttachHandler(Handler handler)
	{
		return boundWidget.addAttachHandler(handler);
	}

	public <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type)
	{
		return boundWidget.addHandler(handler, type);
	}

	public void fireEvent(GwtEvent<?> event)
	{
		boundWidget.fireEvent(event);
	}
	
	public Widget asWidget()
	{
		return boundWidget;
	}
}
