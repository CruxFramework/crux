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
package br.com.sysmap.crux.core.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.utils.StyleUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for gwt widgets. It creates widgets based on a span tag contained
 * in the host HTML page. It provides a declarative way to create widgets.
 * 
 * The following example shows how to create a widget in a declarative way:
 * 
 *  &lt;span id="myWidgetId" _type="textBox" 
 *                           _onclick="myControlClass.myethod" &gt; 
 *  &lt;/span&gt;
 * @author Thiago da Rosa de Bustamante
 */
public abstract class WidgetFactory <T extends Widget>
{
	private static int currentId = 0;
	
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 * @param <W>
	 */
	public static class WidgetFactoryContext<W>
	{
		private W widget;
		private Element element;
		private String widgetId;
		private Map<String, Object> attributes;
		
		WidgetFactoryContext(W widget, Element element, String widgetId)
		{
			this.widget = widget;
			this.element = element;
			this.widgetId = widgetId;
			this.attributes = new HashMap<String, Object>();
		}
		
		public W getWidget()
		{
			return widget;
		}
		public Element getElement()
		{
			return element;
		}
		public String getWidgetId()
		{
			return widgetId;
		}
		public Object getAttribute(String key)
		{
			return attributes.get(key);
		}
		public void setAttribute(String key, Object value)
		{
			this.attributes.put(key, value);
		}
		public void clearAttributes()
		{
			this.attributes.clear();
		}
		public void removeAttribute(String key)
		{
			this.attributes.remove(key);
		}
		public String readWidgetProperty(String propertyName)
		{
			return WidgetFactory.getProperty(element, propertyName);
		}
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	public final T createWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return createWidget(element, widgetId, true);
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	public T createWidget(Element element, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		WidgetFactoryContext<T> context = createContext(element, widgetId, addToScreen);
		if (context != null)
		{
			processAttributes(context);
			processEvents(context);
			processChildren(context);
			postProcess(context);
			return context.getWidget();
		}
		return null;
	}

	/**
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected WidgetFactoryContext<T> createContext(Element element, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		T widget = instantiateWidget(element, widgetId);
		if (widget != null)
		{
			if(addToScreen)
			{
				Screen.add(widgetId, widget);
			}			

			return new WidgetFactoryContext<T>(widget, element, widgetId);
		}
		return null;
	}
	
	/**
	 * Process element children
	 * @param widget
	 * @param parentElement
	 * @param widgetId
	 * @throws InterfaceConfigException 
	 */
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
	}

	/**
	 * Process element children
	 * @param widget
	 * @param parentElement
	 * @param widgetId
	 * @throws InterfaceConfigException 
	 */
	public void postProcess(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
	}

	/**
	 * Process widget attributes
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException 
	 */
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration(value="visible", type=Boolean.class),
		@TagAttributeDeclaration(value="tooltip", supportsI18N=true),
		@TagAttributeDeclaration("style")
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		String styleName = context.readWidgetProperty("styleName");
		if (styleName != null && styleName.length() > 0){
			context.getWidget().setStyleName(styleName);
		}
		String visible = context.readWidgetProperty("visible");
		if (visible != null && visible.length() > 0){
			context.getWidget().setVisible(Boolean.parseBoolean(visible));
		}
		String style = context.readWidgetProperty("style");
		if (style != null && style.length() > 0)
		{
			String[] styleAttributes = style.split(";");
			for (int i=0; i<styleAttributes.length; i++)
			{
				String[] attr = styleAttributes[i].split(":");
				if (attr != null && attr.length == 2)
				{
					StyleUtils.addStyleProperty(context.getWidget().getElement(), attr[0], attr[1]);
				}
			}
		}
		String width = context.readWidgetProperty("width");
		if (width != null && width.length() > 0){
			context.getWidget().setWidth(width);
		}
		String height = context.readWidgetProperty("height");
		if (height != null && height.length() > 0){
			context.getWidget().setHeight(height);
		}
		String tooltip = context.readWidgetProperty("tooltip");
		if (tooltip != null && tooltip.length() > 0)
		{
			context.getWidget().setTitle(ScreenFactory.getInstance().getDeclaredMessage(tooltip));
		}
	}
	
	/**
	 * Process widget events
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException
	 */
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadWidget")
	})
	public void processEvents(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		final Event eventLoad = EvtBind.getWidgetEvent(context.getElement(), "onLoadWidget");
		if (eventLoad != null)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					Events.callEvent(eventLoad, event);
				}
			});
		}
	}
	
	/**
	 * Retrieve a Crux widget attribute from its declaring span element
	 * @param element the widget span metadata element
	 * @param propertyName the name of the attribute
	 * @return attribute value
	 */
	public static String getProperty(Element element, String propertyName)
	{
		if (element != null)
		{
			return element.getAttribute("_"+propertyName);
		}
		else
		{
			return null;
		}
	}
	
	/**Retrieve the widget child element name
	 * @param childElement the span element representing the child
	 * @return child name
	 */
	public static String getChildName(Element childElement)
	{
		if (childElement != null)
		{
			return childElement.getAttribute("__tag");
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Adds an event handler, called when the screen is completely loaded
	 * @param loadHandler
	 */
	protected void addScreenLoadedHandler(ScreenLoadHandler loadHandler)
	{
		Screen.get().addLoadHandler(loadHandler);
	}

	/**
	 * Used by widgets that need to create new widgets as children, like tree. 
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static Widget createChildWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return ScreenFactory.getInstance().newWidget(element, widgetId);
	}

	/**
	 * Creates a sequential id
	 * @return
	 */
	protected static String generateNewId() 
	{
		return "_crux_" + (++currentId );
	}
	
	/**
	 * Returns the element which is the father of the given one. If it does not have an id, creates a random for it
	 * @param child
	 * @return
	 */
	protected Element getParentElement(Element child)
	{
		Element parent = child.getParentElement();
		
		String id = parent.getId();
		if(id == null || id.trim().length() == 0)
		{
			parent.setId(generateNewId());
		}
		
		return parent;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected boolean isWidget(Element element)
	{
		return ScreenFactory.getInstance().isValidWidget(element);
	}
	
	/**
	 * Returns true if an element is a span
	 * @param element
	 * @param acceptsNull
	 * @return
	 */
	private static boolean isSpan(Element element) 
	{
		return element != null && element.getTagName() != null && element.getTagName().equalsIgnoreCase("span");
	}
	
	/**
	 * Returns the element if it is a span. Raises error otherwise.
	 * @param element
	 * @param acceptsNull
	 * @return
	 * @throws InterfaceConfigException
	 */
	private static Element ensureSpan(Element element) throws InterfaceConfigException
	{
		if(isSpan(element))
		{
			return element;
		}
		else
		{
			throw new InterfaceConfigException(Crux.getMessages().widgetFactoryEnsureSpanFail());
		}
	}
	
	/**
	 * If the next child element is a span, returns it. Otherwise, raises error.
	 * If there is no child element and <code>acceptsNull</code> is false, raises error.
	 * @param element
	 * @param acceptsNull
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static Element ensureFirstChildSpan(Element element, boolean acceptsNoChild) throws InterfaceConfigException
	{
		Element firstChild = element.getFirstChildElement();
		
		while (firstChild!= null && firstChild.getTagName() == null)
		{
			firstChild = firstChild.getNextSiblingElement();
		}
		
		if((!acceptsNoChild && firstChild == null) || (firstChild != null && !isSpan(element)))
		{
			throw new InterfaceConfigException(Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty());			
		}
		else
		{
			return firstChild;
		}	
	}
	
	/**
	 * 
	 * @param element
	 * @param acceptsNoChild
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static String ensureTextChild(Element element, boolean acceptsNoChild) throws InterfaceConfigException
	{
		NodeList<Node> childNodes = element.getChildNodes();
		String result = null;
		
		if(childNodes != null)
		{
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				Node node = childNodes.getItem(i);
				
				if(node instanceof Text)
				{
					Text text =  (Text) node;
					result = (result == null?text.getNodeValue():result+text.getNodeValue());
				}
			}
		}
		
		if((result == null || result.length() == 0) && !acceptsNoChild)
		{
			throw new InterfaceConfigException(Crux.getMessages().widgetFactoryEnsureTextChildEmpty());
		}

		return result;
	}
	/**
	 * If there are any span elements among the child nodes of the given one, returns those spans.
	 * If there are no child spans and <code>acceptsNoChild</code> is false, raises error.
	 * @param element
	 * @param acceptsNull
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static List<Element> ensureChildrenSpans(Element element, boolean acceptsNoChild) throws InterfaceConfigException
	{
		List<Element> childSpans = new ArrayList<Element>();
		
		NodeList<Node> childNodes = element.getChildNodes();
		
		if(childNodes != null)
		{
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				Node node = childNodes.getItem(i);
				
				if(node instanceof Element && ((Element)node).getTagName() != null)
				{
					Element elem =  ensureSpan((Element) node);
					childSpans.add(elem);
				}
			}
		}
		
		if(childSpans.size() == 0 && !acceptsNoChild)
		{
			throw new InterfaceConfigException(Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty());
		}
		
		return childSpans;	
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 * @throws InterfaceConfigException 
	 */
	protected Element ensureWidget(Element element) throws InterfaceConfigException
	{
		if(isWidget(element))
		{
			return element;
		}
		
		throw new InterfaceConfigException(Crux.getMessages().widgetFactoryEnsureWidgetFail());
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected static boolean hasWidth(Element element)
	{
		String width = element.getAttribute("_width");
		
		return width != null && (width.length() > 0);
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected static boolean hasHeight(Element element)
	{
		String height = element.getAttribute("_height");
		
		return height != null && (height.length() > 0);
	}
	
	public abstract T instantiateWidget(Element element, String widgetId) throws InterfaceConfigException;
}
