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
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

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
	public void setAccessKey(char accessKey) 
	{
		if (this.accessKey != accessKey)
		{
			modifiedProperties.put("accessKey", Character.toString(accessKey));
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
		if (focusWidget.isEnabled() != enabled)
		{
			modifiedProperties.put("enabled", Boolean.toString(enabled));
			focusWidget.setEnabled(enabled);
		}
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
		if (focusWidget.getTabIndex() != tabIndex)
		{
			modifiedProperties.put("tabIndex", Integer.toString(tabIndex));
			focusWidget.setTabIndex(tabIndex);
		}
	}

	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.trim().length() > 0)
		{
			focusWidget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.trim().length() > 0)
		{
			focusWidget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.trim().length() == 1)
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

		if (clientFormatter != null && widget instanceof HasText)
		{
			focusWidget.addFocusListener(new FocusListener()
			{
				public void onFocus(Widget sender) 
				{
				}
				public void onLostFocus(Widget sender) 
				{
					try 
					{
						setValue(getValue());
					} 
					catch (InvalidFormatException e) 
					{
						Window.alert(e.getLocalizedMessage());
						setValue(null);
					}
				}
			});
		}

		final Event eventFocus = getComponentEvent(element, EventFactory.EVENT_FOCUS);
		final Event eventBlur = getComponentEvent(element, EventFactory.EVENT_BLUR);
		if (eventFocus != null || eventBlur != null)
		{
			FocusListener listener = new FocusListener()
			{
				public void onFocus(Widget sender) 
				{
					if (eventFocus != null) EventFactory.callEvent(eventFocus, getId());
				}

				public void onLostFocus(Widget sender) 
				{
					if (eventBlur != null) EventFactory.callEvent(eventBlur, getId());
				}
			};
			focusWidget.addFocusListener(listener);
		}

		final Event eventClick = getComponentEvent(element, EventFactory.EVENT_CLICK);
		if (eventClick != null)
		{
			ClickListener listener = new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					EventFactory.callEvent(eventClick, getId());
				}
			};
			((SourcesClickEvents)widget).addClickListener(listener);
		}

		final Event eventKeyDown = getComponentEvent(element, EventFactory.EVENT_KEY_DOWN);
		final Event eventKeyPress = getComponentEvent(element, EventFactory.EVENT_KEY_PRESS);
		final Event eventKeyUp = getComponentEvent(element, EventFactory.EVENT_KEY_UP);
		if (eventKeyDown != null || eventKeyPress != null || eventKeyUp != null)
		{
			KeyboardListener listener = new KeyboardListener()
			{
				public void onKeyDown(Widget sender, char keyCode, int modifiers) 
				{
					if (eventKeyDown != null) EventFactory.callEvent(eventKeyDown, getId());
				}

				public void onKeyPress(Widget sender, char keyCode,	int modifiers) 
				{
					if (eventKeyPress != null) EventFactory.callEvent(eventKeyPress, getId());
				}

				public void onKeyUp(Widget sender, char keyCode, int modifiers) 
				{
					if (eventKeyUp != null) EventFactory.callEvent(eventKeyUp, getId());
				}
			};
			focusWidget.addKeyboardListener(listener);
		}
	}
	
	/**
	 * Update component attributes
	 * @see #Component.renderAttributes
	 */
	protected void updateAttributes(com.google.gwt.xml.client.Element element)
	{
		super.updateAttributes(element);

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.trim().length() > 0)
		{
			focusWidget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.trim().length() > 0)
		{
			focusWidget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.trim().length() == 1)
		{
			this.accessKey = accessKey.charAt(0);
			focusWidget.setAccessKey(this.accessKey);
		}
	}
}
