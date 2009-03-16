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

import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.JSEngine;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.formatter.ClientFormatter;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
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
	protected String property;
	protected String width;
	protected String height;
	protected String formatter;
	protected String tooltip;
	protected ClientFormatter clientFormatter = null;
	protected boolean visible;
	
	protected Map<String, String> modifiedProperties = new HashMap<String, String>();
	
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
	 * Builds an Event object from the XML DOM element representing the component (Its <span> tag)
	 * @param element
	 * @param evtId
	 * @return
	 */
	protected Event getComponentEvent(com.google.gwt.xml.client.Element element, String evtId)
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
	 * Update the component with information sent by server.
	 * @param element
	 */
	protected void update(com.google.gwt.xml.client.Element element) 
	{
		updateAttributes(element);
	}
	
	/**
	 * Render component attributes
	 * @param element page DOM element representing the component (Its <span> tag)
	 */
	protected void renderAttributes(Element element)
	{
		String width = element.getAttribute("_width");
		if (width != null && width.trim().length() > 0)
			widget.setWidth(width);
		
		String height = element.getAttribute("_height");
		if (height != null && height.trim().length() > 0)
			widget.setHeight(height);
		
		String visible = element.getAttribute("_visible");
		if (visible != null && visible.trim().length() > 0)
			widget.setVisible(Boolean.parseBoolean(visible));

		String tooltip = element.getAttribute("_tooltip");
		if (tooltip != null && tooltip.trim().length() > 0)
			widget.setTitle(tooltip);

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
		formatter = element.getAttribute("_formatter");
		if (formatter != null && formatter.trim().length() > 0)
		{
			clientFormatter = ScreenFactory.getInstance().getClientFormatter(formatter);
			if (clientFormatter == null)
			{
				Window.alert(JSEngine.messages.componentFormatterNotFound(formatter));
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
			String text = element.getAttribute("_value");
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
	protected void attachEvents(Element element)
	{
		if (widget instanceof SourcesChangeEvents)
		{
			final Event event = getComponentEvent(element, EventFactory.EVENT_CHANGE);
			if (event != null)
			{
				ChangeListener listener = new ChangeListener()
				{
					public void onChange(Widget sender) 
					{
						EventFactory.callEvent(event, getId());
					}
				};
				((SourcesChangeEvents)widget).addChangeListener(listener);
			}
		}
	}

	/**
	 * update component attibutes
	 * @param element page XML element representing the component (Its <span> tag)
	 */
	protected void updateAttributes(com.google.gwt.xml.client.Element element) 
	{
		String width = element.getAttribute("_width");
		if (width != null && width.trim().length() > 0)
			widget.setWidth(width);
		
		String height = element.getAttribute("_height");
		if (height != null && height.trim().length() > 0)
			widget.setHeight(height);

		String visible = element.getAttribute("_visible");
		if (visible != null && visible.trim().length() > 0)
			widget.setVisible(Boolean.parseBoolean(visible));
		
		String tooltip = element.getAttribute("_tooltip");
		if (tooltip != null && tooltip.trim().length() > 0)
			widget.setTitle(tooltip);

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
			String innerHtml = element.getNodeValue();
			if (innerHtml != null && innerHtml.trim().length() > 0)
			{
				((HasHTML)widget).setHTML(innerHtml);
			}
		}
		if (widget instanceof HasText)
		{
			String text = element.getAttribute("_value");
			if (text != null && text.trim().length() > 0)
				((HasText)widget).setText(text);
		}
		if (widget instanceof HasName)
		{
			String name = element.getAttribute("_name");
			if (name != null && name.trim().length() > 0)
				((HasName)widget).setName(name);
		}
		
		String property = element.getAttribute("_property");
		ScreenFactory.getInstance().getScreen().addProperty(property, getId());
	}
	
	/**
	 * Return the component value without any format changes. Used for value serialization in server calls
	 * @return
	 */
	protected String getSerializedValue() 
	{
		if (widget instanceof HasText)
		{
			return ((HasText)widget).getText();
		}
		return null;
	}

	/**
	 * Return the component value applying any format changes performed by it's formatter
	 * @return
	 */
	public Object getValue() throws InvalidFormatException 
	{
		if (widget instanceof HasText)
		{
			String text = ((HasText)widget).getText();
			if (clientFormatter != null)
			{
				return clientFormatter.unformat(text);
			}
			return text;
		}
		
		return null;
	}

	/**
	 * Set the component value applying any format changes performed by it's formatter
	 * @return
	 */
	public void setValue(Object value) 
	{
		if (widget instanceof HasText)
		{
			if (clientFormatter != null)
			{
				String text = clientFormatter.mask(clientFormatter.format(value));
				((HasText)widget).setText(text);
			}
			else
			{
				((HasText)widget).setText(value!=null?value.toString():"");
			}
		}
	}

	/**
	 * Return property associated with this component
	 * @return
	 */
	public String getProperty() 
	{
		return this.property;
	}

	/**
	 * Associate a property with this component
	 * @return
	 */
	protected void setProperty(String property) 
	{
		this.property = property;	
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
	 * Serializes the component to be sent to server.
	 * @param builder
	 */
	protected void serialize(StringBuilder builder) 
	{
		if (modifiedProperties.size()>0)
		{
			boolean first = true;
			for (String property : modifiedProperties.keySet()) 
			{
				if (!first)
				{
					builder.append("&");
				}
				first = false;
				ScreenSerialization.buildPostParameter(builder, "c("+getId()+")."+property, modifiedProperties.get(property));
			}
		}
	}

	/**
	 * Called by ScreenSerialization to inform that the component serialization was done successfully 
	 */
	protected void confirmSerialization()
	{
		modifiedProperties.clear();
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
		if ((this.width != null && width == null) || (this.width == null && width != null) ||
			(this.width != null && width != null && !this.width.equals(width)))
		{
			modifiedProperties.put("width", (width!=null)?width:"");
			widget.setWidth(width);
			this.width = width;
		}
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
		String classNameAtual = widget.getStyleName();
		
		if ((classNameAtual != null && className == null) || (classNameAtual == null && className != null) ||
			(classNameAtual != null && className != null && !classNameAtual.equals(className)))
		{
			modifiedProperties.put("className", (className!=null)?className:"");
			widget.setStyleName(className);
		}
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
		if ((this.height != null && height == null) || (this.height == null && height != null) ||
			(this.height != null && height != null && !this.height.equals(height)))
		{
			modifiedProperties.put("height", (height!=null)?height:"");
			widget.setHeight(height);
			this.height = height;
		}
	}
	
	/**
	 * Return the component's visibility
	 * @return
	 */
	public boolean isVilible()
	{
		return visible;
	}
	
	/**
	 * Set the component's visibility
	 * @return
	 */
	public void setVisible(boolean visible)
	{
		if (this.visible != visible)
		{
			modifiedProperties.put("visible", Boolean.toString(visible));
			widget.setVisible(visible);
			this.visible = visible;
		}
	}
	
	/**
	 * Return component's tooltip
	 * @return
	 */
	public String getTooltip() 
	{
		return tooltip;
	}

	/**
	 * Set component's tooltip
	 * @return
	 */
	public void setTooltip(String tooltip) 
	{
		if ((this.tooltip != null && tooltip == null) || (this.tooltip == null && tooltip != null) ||
			(this.tooltip != null && tooltip != null && !this.tooltip.equals(tooltip)))
		{
			modifiedProperties.put("tooltip", (tooltip!=null)?tooltip:"");
			widget.setTitle(tooltip);
			this.tooltip = tooltip;
		}
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
