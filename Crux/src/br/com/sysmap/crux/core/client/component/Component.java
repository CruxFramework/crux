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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represent a CRUX component at the application's client side. 
 * @author Thiago Bustamante
 */
public abstract class Component extends UIObject
{
	private int hashValue = 0;
	protected Widget widget;
	protected String id;
	protected Screen screen = null;
	
	/**
	 * Constructor
	 * @param id identifies the component. Components can be retrieved form the Screen object using this field.
	 * @param widget GWT widget wrapped by this class. 
	 */
	protected Component(String id, Widget widget) 
	{
		super(widget);
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
		if (width != null && width.length() > 0)
		{
			setWidth(width);
		}
		String height = element.getAttribute("_height");
		if (height != null && height.length() > 0)
		{
			setHeight(height);
		}
		String visible = element.getAttribute("_visible");
		if (visible != null && visible.length() > 0)
		{
			setVisible(Boolean.parseBoolean(visible));
		}
		String tooltip = element.getAttribute("_tooltip");
		if (tooltip != null && tooltip.length() > 0)
		{
			setTitle(tooltip);
		}
		String classAttr = element.getAttribute("_class");
		if (classAttr != null && classAttr.length() > 0)
		{
			setStyleName(classAttr);
		}
		String style = element.getAttribute("_style");
		if (style != null && style.length() > 0)
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
			if (innerHtml != null && innerHtml.length() > 0)
			{
				((HasHTML)widget).setHTML(innerHtml);
				element.setInnerHTML("");
			}
		}
		if (widget instanceof HasText)
		{
			String text = element.getAttribute("_text");
			if (text != null && text.length() > 0)
				((HasText)widget).setText(text);
		}
		if (widget instanceof HasName)
		{
			String name = element.getAttribute("_name");
			if (name != null && name.length() > 0)
				((HasName)widget).setName(name);
		}
	}
	
	/**
	 * Render component events
	 * @param element page DOM element representing the component (Its <span> tag)
	 */
	protected void attachEvents(Element element)
	{

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
	 * Used by components that need to create new components as children, like tree. Tree can container 
	 * multiple components.
	 * 
	 * @param element
	 * @param componentId
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected Component createChildComponent(Element element, String componentId) throws InterfaceConfigException
	{
		return ScreenFactory.getInstance().newComponent(element, componentId);
	}
	
	/**
	 * Provide access to component's widget. Used for component subclasses that
	 * need to access widgets of their children. 
	 * @param component
	 * @return
	 */
	protected Widget getComponentWidget(Component component)
	{
		return (component.widget!=null?component.widget:null);
	}	
}
