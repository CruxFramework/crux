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
package br.com.sysmap.crux.core.client.component;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represent a CRUX component at the application's client side. 
 * @author Thiago Bustamante
 */
public class Component
{
	private int hashValue = 0;
	protected Widget widget;
	protected String id;
	protected String width;
	protected String height;

	protected Screen screen = null;
	
	/**
	 * Constructor
	 * @param id identifies the component. Components can be retrieved form the Screen object using this field.
	 * @param widget GWT widget wrapped by this class. 
	 */
	public Component(String id, Widget widget) 
	{
		if (widget == null) throw new NullPointerException();
		this.id = id;
		this.widget = widget;
	}
	
	/**
	 * Return screen that contains this component
	 * @return
	 */
	public Screen getScreen() 
	{
		return screen;
	}

	/**
	 * Set screen that contains this component
	 * @return
	 */
	protected void setScreen(Screen screen) 
	{
		this.screen = screen;
	}

	/**
	 * Builds an Event object from the page DOM element representing the component (Its <span> tag)
	 * @param element
	 * @param evtId
	 * @return
	 */
	protected Event getComponentEvent(Element element, String evtId)
	{
		String evt = element.getAttribute(evtId);
		return EventFactory.getEvent(evtId, evt);
	}
	
	/**
	 * 
	 * @return id
	 */
	public String getId() 
	{
		return this.id;
	}
	
	/**
	 * Render component into the screen
	 * @param element
	 */
	protected void render(Element element) 
	{
		renderAttributes(element);
		attachEvents(element);
	}

	/**
	 * Render component attributes
	 * @param element page DOM element representing the component (Its <span> tag)
	 */
	protected void renderAttributes(Element element)
	{
		String width = element.getAttribute("_width");
		if (width != null && width.trim().length() > 0)
		{
			this.width = width;
			widget.setWidth(this.width);
		}
		String height = element.getAttribute("_height");
		if (height != null && height.trim().length() > 0)
		{
			this.height = height;
			widget.setHeight(this.height);
		}
		String visible = element.getAttribute("_visible");
		if (visible != null && visible.trim().length() > 0)
		{
			widget.setVisible(Boolean.parseBoolean(visible));
		}
		String tooltip = element.getAttribute("_tooltip");
		if (tooltip != null && tooltip.trim().length() > 0)
		{
			widget.setTitle(tooltip);
		}
		
		String classAttr = element.getAttribute("_class");
		if (classAttr != null && classAttr.trim().length() > 0)
			widget.setStyleName(classAttr);
		
		String style = element.getAttribute("_style");
		if (style != null && style.trim().length() > 0)
		{
			String[] styleAttributes = style.split(";");
			for (int i=0; i<styleAttributes.length; i++)
			{
				String[] attr = styleAttributes[i].split(":");
				if (attr != null && attr.length == 2)
					DOM.setStyleAttribute(widget.getElement(), attr[0], attr[1]);
			}
		}
		
		if (widget instanceof HasHTML)
		{
			String innerHtml = element.getInnerHTML();
			if (innerHtml != null && innerHtml.trim().length() > 0)
			{
				((HasHTML)widget).setHTML(innerHtml);
				element.setInnerHTML("");
			}
		}
		if (widget instanceof HasText)
		{
			String text = element.getAttribute("_text");
			if (text != null && text.trim().length() > 0)
				((HasText)widget).setText(text);
		}
		if (widget instanceof HasName)
		{
			String name = element.getAttribute("_name");
			if (name != null && name.trim().length() > 0)
				((HasName)widget).setName(name);
		}
	}
	
	/**
	 * Render component events
	 * @param element page DOM element representing the component (Its <span> tag)
	 */
	@SuppressWarnings("unchecked")
	protected void attachEvents(Element element)
	{
		if (widget instanceof HasValueChangeHandlers)
		{
			final Event event = getComponentEvent(element, EventFactory.EVENT_CHANGE);
			if (event != null)
			{
				ValueChangeHandler handler = new ValueChangeHandler()
				{
					public void onValueChange(ValueChangeEvent valEvent) 
					{
						EventFactory.callEvent(event, getId());
					}
				};
				((HasValueChangeHandlers)widget).addValueChangeHandler(handler);
			}
		}
	}
	
	/**
	 * Adds an event handler, called when the screen is completely loaded
	 * @param loadHandler
	 */
	protected void addScreenLoadedHandler(ScreenLoadHandler loadHandler)
	{
		getScreen().addLoadHandler(loadHandler);
	}

	public boolean equals(Object obj) 
	{
    	if (obj == null) return false;
    	if (!(obj instanceof Component)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((Component)obj).getId();
    	return (compId1 == null?compId2==null:compId1.equals(compId2));
    }
    
	public int hashCode()
    {
        if (this.hashValue == 0)
        {
            int result = 17;
            String compStr = this.getId();
            int idComp = compStr == null ? 0 : compStr.hashCode();
            result = result * 37 + idComp;
            this.hashValue = result;
        }
        return this.hashValue;
    }

	/**
	 * Return component's width
	 * @return
	 */
	public String getWidth() 
	{
		return width;
	}

	/**
	 * Set component's width
	 * @return
	 */
	public void setWidth(String width) 
	{
		widget.setWidth(width);
		this.width = width;
	}

	/**
	 * Return component's className
	 * @return
	 */
	public void getClassName()
	{
		widget.getStyleName();
	}
	
	/**
	 * Set component's className
	 * @return
	 */
	public void setClassName(String className) 
	{
		widget.setStyleName(className);
	}

	/**
	 * Adds a secondary style class
	 * @param style
	 */
	public void addClassName(String style) 
	{
		widget.addStyleName(style);
	}

	/**
	 * Removes a style class name
	 * @param style
	 */
	public void removeStyleName(String style)
	{
		widget.removeStyleName(style);
	}
	
	/**
	 * Return component's height
	 * @return
	 */
	public String getHeight() 
	{
		return height;
	}

	/**
	 * Set component's height
	 * @return
	 */
	public void setHeight(String height) 
	{
		widget.setHeight(height);
		this.height = height;
	}
	
	/**
	 * Return the component's visibility
	 * @return
	 */
	public boolean isVilible()
	{
		return widget.isVisible();
	}
	
	/**
	 * Set the component's visibility
	 * @return
	 */
	public void setVisible(boolean visible)
	{
		widget.setVisible(visible);
	}
	
	/**
	 * Return component's tooltip
	 * @return
	 */
	public String getTooltip() 
	{
		return widget.getTitle();
	}

	/**
	 * Set component's tooltip
	 * @return
	 */
	public void setTooltip(String tooltip) 
	{
		widget.setTitle(tooltip);
	}
	
	/**
	 * Return the component's offset width in pixels
	 * @return
	 */
	public int getOffsetWidth()
	{
		return widget.getOffsetWidth();
	}
	
	/**
	 * Return the component's offset height in pixels
	 * @return
	 */
	public int getOffsetHeight() 
	{	
		return widget.getOffsetHeight();
	}
	
	/**
	 * Return the object's absolute left position in pixels
	 * @return
	 */
	public int getAbsoluteLeft() 
	{
		return widget.getAbsoluteLeft();
	}
	
	/**
	 * Return the object's absolute top position in pixels
	 * @return
	 */
	public int getAbsoluteTop() 
	{
		return widget.getAbsoluteTop();
	}
}
