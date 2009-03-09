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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesFocusEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for build concrete CRUX components
 * @author Thiago
 *
 */
public class Component
{
	private int hashValue = 0;
	protected Widget widget;
	protected String id;
	protected String serverBind;
	protected String width;
	protected String height;
	protected String formatter;
	ClientFormatter clientFormatter = null;
	
	protected Map<String, String> modifiedProperties = new HashMap<String, String>();
	
	public Component(String id, Widget widget) 
	{
		if (widget == null) throw new NullPointerException();
		this.id = id;
		this.widget = widget;
	}
	
	protected Event getComponentEvent(Element element, String evtId)
	{
		String evt = element.getAttribute(evtId);
		return EventFactory.getEvent(evtId, evt);
	}
	
	protected Event getComponentEvent(com.google.gwt.xml.client.Element element, String evtId)
	{
		String evt = element.getAttribute(evtId);
		return EventFactory.getEvent(evtId, evt);
	}
	
	public String getId() 
	{
		return this.id;
	}
	
	protected void render(Element element) 
	{
		renderAttributes(element);
		attachEvents(element);
		
	}

	protected void update(com.google.gwt.xml.client.Element element) 
	{
		updateAttributes(element);
	}
	
	protected void renderAttributes(Element element)
	{
		String width = element.getAttribute("_width");
		if (width != null && width.trim().length() > 0)
			widget.setWidth(width);
		
		String height = element.getAttribute("_height");
		if (height != null && height.trim().length() > 0)
			widget.setHeight(height);
		
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
	
	protected void attachEvents(Element element)
	{
		if (widget instanceof SourcesClickEvents)
		{
			final Event event = getComponentEvent(element, EventFactory.EVENT_CLICK);
			if (event != null)
			{
				ClickListener listener = new ClickListener()
				{
					public void onClick(Widget sender) 
					{
						EventFactory.callEvent(event, getId());
					}
				};
				((SourcesClickEvents)widget).addClickListener(listener);
			}
		}
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
		if (widget instanceof SourcesFocusEvents)
		{
			if (clientFormatter != null && widget instanceof HasText)
			{
				((SourcesFocusEvents)widget).addFocusListener(new FocusListener()
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
				((SourcesFocusEvents)widget).addFocusListener(listener);
			}
		}
	}

	protected void updateAttributes(com.google.gwt.xml.client.Element element) 
	{
		String width = element.getAttribute("_width");
		if (width != null && width.trim().length() > 0)
			widget.setWidth(width);
		
		String height = element.getAttribute("_height");
		if (height != null && height.trim().length() > 0)
			widget.setHeight(height);
		
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
		
		String serverBind = element.getAttribute("_serverBind");
		ScreenFactory.getInstance().getScreen().addBeanProperty(serverBind, getId());
	}
	
	protected String getSerializedValue() 
	{
		if (widget instanceof HasText)
		{
			return ((HasText)widget).getText();
		}
		return null;
	}

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

	public String getServerBind() 
	{
		return this.serverBind;
	}

	protected void setServerBind(String serverBind) 
	{
		this.serverBind = serverBind;	
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

	protected void confirmSerialization()
	{
		modifiedProperties.clear();
	}

	public String getWidth() 
	{
		return width;
	}

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

	public void getClassName()
	{
		widget.getStyleName();
	}
	
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

	public String getHeight() 
	{
		return height;
	}

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
}
