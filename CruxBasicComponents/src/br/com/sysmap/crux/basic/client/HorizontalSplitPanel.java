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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HorizontalSplitPanelImages;

/**
 * Represents a HorizontalSplitPanel
 * @author Thiago Bustamante
 */
public class HorizontalSplitPanel extends SplitPanel
{
	protected com.google.gwt.user.client.ui.HorizontalSplitPanel splitPanelWidget;
	protected Component left = null;
	protected Component right = null;

	public HorizontalSplitPanel(String id, Element element) 
	{
		this(id, createSplitWidget(id, element));
	}

	public HorizontalSplitPanel(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.HorizontalSplitPanel());
	}

	public HorizontalSplitPanel(String id, HorizontalSplitPanelImages images) 
	{
		this(id, new com.google.gwt.user.client.ui.HorizontalSplitPanel(images));
	}

	protected HorizontalSplitPanel(String id, com.google.gwt.user.client.ui.HorizontalSplitPanel widget) 
	{
		super(id, widget);
		this.splitPanelWidget = widget;
	}

	public boolean isResizing() 
	{
		return this.splitPanelWidget.isResizing();
	}

	/**
	 * Moves the position of the splitter.
	 * 
	 * @param size the new size of the left region in CSS units (e.g. "10px",
	 *            "1em")
	 */
	public void setSplitPosition(String size)
	{
		this.splitPanelWidget.setSplitPosition(size);
	}

	/**
	 * Gets the component in the pane that is at the end of the line
	 * direction for the layout. That is, in an RTL layout, gets
	 * the component in the left pane, and in an LTR layout, gets
	 * the component in the right pane.
	 *
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getEndOfLineComponent() 
	{
		if (LocaleInfo.getCurrentLocale().isRTL())
		{
			return getLeftComponent();
		}
		else
		{
			return getRightComponent();
		}
	}

	/**
	 * Gets the component in the left side of the panel.
	 * 
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getLeftComponent() 
	{
		return left;
	}

	/**
	 * Gets the component in the right side of the panel.
	 * 
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getRightComponent() 
	{
		return right;
	}

	/**
	 * Gets the component in the pane that is at the start of the line 
	 * direction for the layout. That is, in an RTL environment, gets
	 * the component in the right pane, and in an LTR environment, gets
	 * the component in the left pane.   
	 *
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getStartOfLineComponent() 
	{
		if (LocaleInfo.getCurrentLocale().isRTL())
		{
			return getRightComponent();
		}
		else
		{
			return getLeftComponent();
		}
	}

	/**
	 * Sets the component in the pane that is at the end of the line direction
	 * for the layout. That is, in an RTL layout, sets the component in
	 * the left pane, and in and RTL layout, sets the component in the 
	 * right pane.
	 *
	 * @param c the component
	 */
	public void setEndOfLineComponent(Component c) 
	{
		if (LocaleInfo.getCurrentLocale().isRTL())
		{
			setLeftComponent(c);
		}
		else
		{
			setRightComponent(c);
		}
	}

	/**
	 * Sets the component in the left side of the panel.
	 * 
	 * @param c the Component
	 */
	public void setLeftComponent(Component c) 
	{
		left = c;
		splitPanelWidget.setLeftWidget(getComponentWidget(c));
	}

	/**
	 * Sets the component in the right side of the panel. 
	 * 
	 * @param c the Component
	 */
	public void setRightComponent(Component c) 
	{    
		right = c;
		splitPanelWidget.setRightWidget(getComponentWidget(c));
	}

	/**
	 * Sets the component in the pane that is at the start of the line direction
	 * for the layout. That is, in an RTL layout, sets the component in
	 * the right pane, and in and RTL layout, sets the component in the
	 * left pane.
	 *
	 * @param c the Component
	 */
	public void setStartOfLineComponent(Component c) 
	{
		if (LocaleInfo.getCurrentLocale().isRTL())
		{
			setRightComponent(c);
		}
		else
		{
			setLeftComponent(c);
		}
	}

	/**
	 * Creates the Split widget
	 * @param element
	 * @return
	 */
	private static com.google.gwt.user.client.ui.HorizontalSplitPanel createSplitWidget(String id, Element element)
	{
		Event eventLoadImage = EvtBind.getComponentEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			HorizontalSplitPanelImages splitImages = (HorizontalSplitPanelImages) EventFactory.callEvent(eventLoadImage, id);

			return new com.google.gwt.user.client.ui.HorizontalSplitPanel(splitImages);
		}
		return new com.google.gwt.user.client.ui.HorizontalSplitPanel();
	}

	@Override
	protected void renderSplitItem(Element element) throws InterfaceConfigException
	{
		String position = element.getAttribute("_position");
		String id = element.getAttribute("id");

		Element e = getComponentChildElement(element);
		if (e != null)
		{
			if (position.equals("left"))
			{

				setLeftComponent(createChildComponent(e, id));
			}
			else if (position.equals("right"))
			{
				setRightComponent(createChildComponent(e, id));
			}
		}
	}
}
