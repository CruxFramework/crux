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
import java.util.List;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
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
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public abstract class WidgetFactory <T extends Widget>
{
	private static int currentId = 0;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 * @param <W>
	 */
	public static class WidgetFactoryContext<W>
	{
		private W widget;
		private Element element;
		private String widgetId;
		
		private WidgetFactoryContext(W widget, Element element, String widgetId)
		{
			this.widget = widget;
			this.element = element;
			this.widgetId = widgetId;
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
	
	public T createWidget(Element element, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		T widget = instantiateWidget(element, widgetId);
		if (widget != null)
		{
			if(addToScreen)
			{
				Screen.add(widgetId, widget);
			}			

			WidgetFactoryContext<T> context = new WidgetFactoryContext<T>(widget, element, widgetId);
			processAttributes(context);
			processEvents(context);
			processChildren(context);
		}
		return widget;
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
	 * Process widget attributes
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException 
	 */
	@TagAttributes({
		@TagAttribute("width"),
		@TagAttribute("height"),
		@TagAttribute("styleName"),
		@TagAttribute(value="visible", type=Boolean.class),
		@TagAttribute(value="tooltip", autoProcess=false),
		@TagAttribute(value="style", autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		String tooltip = context.getElement().getAttribute("_tooltip");
		if (tooltip != null && tooltip.length() > 0)
		{
			context.getWidget().setTitle(tooltip);
		}
		String style = context.getElement().getAttribute("_style");
		if (style != null && style.length() > 0)
		{
			String[] styleAttributes = style.split(";");
			for (int i=0; i<styleAttributes.length; i++)
			{
				String[] attr = styleAttributes[i].split(":");
				if (attr != null && attr.length == 2)
					DOM.setStyleAttribute((com.google.gwt.user.client.Element) context.getElement(), attr[0], attr[1]);
			}
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
		final Event eventLoad = EvtBind.getWidgetEvent(context.getElement(), Events.EVENT_LOAD_WIDGET);
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
	protected Widget createChildWidget(Element element, String widgetId) throws InterfaceConfigException
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
	private boolean isSpan(Element element) 
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
	private Element ensureSpan(Element element) throws InterfaceConfigException
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
	protected Element ensureFirstChildSpan(Element element, boolean acceptsNoChild) throws InterfaceConfigException
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
	 * If there are any span elements among the child nodes of the given one, returns those spans.
	 * If there are no child spans and <code>acceptsNoChild</code> is false, raises error.
	 * @param element
	 * @param acceptsNull
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected List<Element> ensureChildrenSpans(Element element, boolean acceptsNoChild) throws InterfaceConfigException
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
	
	public abstract T instantiateWidget(Element element, String widgetId) throws InterfaceConfigException;	
}
