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
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.VerticalSplitPanelImages;

/**
 * Represents a VerticalSplitPanel
 * @author Thiago Bustamante
 */
public class VerticalSplitPanel extends SplitPanel
{
	protected com.google.gwt.user.client.ui.VerticalSplitPanel splitPanelWidget;
	protected Component bottom = null;
	protected Component top = null;
	
	public VerticalSplitPanel(String id, Element element) 
	{
		this(id, createSplitWidget(id, element));
	}


	public VerticalSplitPanel(String id, VerticalSplitPanelImages images) 
	{
		this(id, new com.google.gwt.user.client.ui.VerticalSplitPanel(images));
	}
	
	public VerticalSplitPanel(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.VerticalSplitPanel());
	}

	protected VerticalSplitPanel(String id, com.google.gwt.user.client.ui.VerticalSplitPanel widget) 
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
	 * Gets the component in the bottom of the panel.
	 * 
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getBottomComponent() 
	{
		return bottom;
	}

	/**
	 * Gets the component in the top of the panel.
	 * 
	 * @return the component, <code>null</code> if there is not one.
	 */
	public Component getTopComponent() 
	{
		return top;
	}

	/**
	 * Sets the component in the top of the panel.
	 * 
	 * @param c the Component
	 */
	public void setTopComponent(Component c) 
	{
		top = c;
		splitPanelWidget.setTopWidget(getComponentWidget(c));
	}

	/**
	 * Sets the component in the right side of the panel. 
	 * 
	 * @param c the Component
	 */
	public void setBottomComponent(Component c) 
	{    
		bottom = c;
		splitPanelWidget.setBottomWidget(getComponentWidget(c));
	}
	
	/**
	 * Creates the Split widget
	 * @param element
	 * @return
	 */
	private static com.google.gwt.user.client.ui.VerticalSplitPanel createSplitWidget(String id, Element element)
	{
		Event eventLoadImage = EvtBind.getComponentEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			VerticalSplitPanelImages splitImages = (VerticalSplitPanelImages) EventFactory.callEvent(eventLoadImage, id);

			return new com.google.gwt.user.client.ui.VerticalSplitPanel(splitImages);
		}
		return new com.google.gwt.user.client.ui.VerticalSplitPanel();
	}

	@Override
	protected void renderSplitItem(Element element) throws InterfaceConfigException
	{
		String position = element.getAttribute("_position");
		String id = element.getAttribute("id");

		Element e = getComponentChildElement(element);
		if (e != null)
		{
			if (position.equals("top"))
			{

				setTopComponent(createChildComponent(e, id));
			}
			else if (position.equals("bottom"))
			{
				setBottomComponent(createChildComponent(e, id));
			}
		}
	}
}
