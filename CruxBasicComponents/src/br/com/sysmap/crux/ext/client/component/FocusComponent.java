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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * This is the base class for components that can receive focus. 
 * 
 * @author Thiago Bustamante
 *
 */
public class FocusComponent extends Component 
{
	protected FocusWidget focusWidget;
	protected char accessKey;

	/**
	 * Constructor
	 * @param id
	 * @param widget
	 */
	public FocusComponent(String id, FocusWidget widget) 
	{
		super(id, widget);
		focusWidget = (FocusWidget)widget;
	}

	/**
	 * Return the component tabIndex
	 * @return tabIndex
	 */
	public int getTabIndex() 
	{
		return focusWidget.getTabIndex();
	}

	/**
	 * Return true if the component is enabled
	 * @return enabled
	 */
	public boolean isEnabled() 
	{
		return focusWidget.isEnabled();
	}

	/**
	 * Return the component accessKey
	 * @return accessKey
	 */
	public char getAccessKey() 
	{
		return accessKey;
	}

	/**
	 * Set the component accessKey
	 * @param accessKey
	 */
	public void setAccessKey(char accessKey) 
	{
		if (this.accessKey != accessKey)
		{
			focusWidget.setAccessKey(accessKey);
			this.accessKey = accessKey;
		}
	}

	/**
	 * Set the component enabled property
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) 
	{
		focusWidget.setEnabled(enabled);
	}

	/**
	 * Put the focus on component or remove the focus from it
	 * @param focused
	 */
	public void setFocus(boolean focused) 
	{
		focusWidget.setFocus(focused);
	}

	/**
	 * Set the component tabIndex
	 * @param tabIndex
	 */
	public void setTabIndex(int tabIndex) 
	{
		focusWidget.setTabIndex(tabIndex);
	}

	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			focusWidget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.length() > 0)
		{
			focusWidget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			this.accessKey = accessKey.charAt(0);
			focusWidget.setAccessKey(this.accessKey);
		}
	}

	/**
	 * Render component events
	 * @see #Component.attachEvents
	 */
	protected void attachEvents(Element element)
	{	 
		super.attachEvents(element);

		FocusEvtBind.bindEvents(element, focusWidget, getId());
		ClickEvtBind.bindEvent(element, focusWidget, getId());
		KeyEvtBind.bindEvents(element, focusWidget, getId());
		MouseEvtBind.bindEvents(element, focusWidget, getId());
	}
}
