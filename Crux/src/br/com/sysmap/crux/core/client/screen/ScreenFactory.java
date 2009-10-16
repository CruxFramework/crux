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
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.RegisteredClientFormatters;
import br.com.sysmap.crux.core.client.i18n.DeclaredI18NMessages;
import br.com.sysmap.crux.core.client.utils.DOMUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for CRUX screen. Based in the type (extracted from _type attribute in widget declaration span tag), 
 * determine witch class to create for all screen widgets.
 * @author Thiago
 *
 */
public class ScreenFactory {
	
	private static ScreenFactory instance = null;
	 
	private Screen screen = null;
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredWidgetFactories registeredWidgetFactories = null;
	private RegisteredDataSources registeredDataSources = null;
	private DeclaredI18NMessages declaredI18NMessages = null;
	private String screenId = null;
	
	private ScreenFactory()
	{
		this.declaredI18NMessages = GWT.create(DeclaredI18NMessages.class);
		this.registeredDataSources = GWT.create(RegisteredDataSources.class);
	}
	
	/**
	 * Retrieve the ScreenFactory instance.
	 * Is not synchronized, but it is not a problem. The screen is always build on a single thread
	 * @return
	 */
	public static ScreenFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ScreenFactory();
		}
		return instance;
	}
	
	/**
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen()
	{
		if (screen == null)
		{
			this.registeredWidgetFactories = (RegisteredWidgetFactories) GWT.create(RegisteredWidgetFactories.class);
			create();
			this.registeredWidgetFactories = null; // Release resources from javascript memory.
		}
		return screen;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getScreenId()
	{
		if (screenId == null)
		{
			String fileName = DOMUtils.getDocumentName();
			int indexBeg = fileName.indexOf(GWT.getModuleName());
			int indexEnd = fileName.indexOf("?");
			int begin = (indexBeg == -1) ? 0 : indexBeg;
			int end = (indexEnd == -1) ? fileName.length() : indexEnd;
			screenId = fileName.substring(begin, end);
		}
		return screenId;
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public Formatter getClientFormatter(String formatter)
	{
		if (this.registeredClientFormatters == null)
		{
			this.registeredClientFormatters = (RegisteredClientFormatters) GWT.create(RegisteredClientFormatters.class);
		}

		return this.registeredClientFormatters.getClientFormatter(formatter);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getDeclaredMessage(String key)
	{
		return this.declaredI18NMessages.getMessage(key);
	}
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	public DataSource<?> getDataSource(String dataSource)
	{
		return this.registeredDataSources.getDataSource(dataSource);
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	Widget newWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String type = element.getAttribute("_type");
		WidgetFactory<? extends Widget> widgetFactory = registeredWidgetFactories.getWidgetFactory(type);
		if (widgetFactory == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetFactoryNotFound(type));
		}
		
		Widget widget = widgetFactory.createWidget(element, widgetId); 
		if (widget == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryErrorCreateWidget(widgetId));
		}
		
		return widget;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean isValidWidget(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String type = element.getAttribute("_type");
			if (type != null && type.length() > 0 && !"screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	private boolean isScreenDefinitions(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String type = element.getAttribute("_type");
			if (type != null && "screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private void create()
	{
		screen = new Screen(getScreenId());
		Element body = RootPanel.getBodyElement();
		NodeList<Element> spanElements = body.getElementsByTagName("span");
		List<String> widgetIds = new ArrayList<String>();
		Element screenElement = null;
		
		for (int i=0; i<spanElements.getLength(); i++)
		{
			Element element = spanElements.getItem(i);
			if (isScreenDefinitions(element))
			{
				screenElement = element;
				screen.parse(screenElement);
			}
			else if (isValidWidget(element))
			{
				widgetIds.add(element.getId());
			}
		}
		
		List<Element> widgets = new ArrayList<Element>();
		for (String elementId:  widgetIds)
		{
			Element element = DOM.getElementById(elementId);
			try 
			{
				createWidget(element, screen, widgets);
			}
			catch (Throwable e) 
			{
				Crux.getErrorHandler().handleError(e);
				element.setInnerText(Crux.getMessages().screenFactoryGenericErrorCreateWidget(e.getLocalizedMessage()));
			}
		}
		if (screenElement != null)
		{
			clearScreenMetaTag(screenElement);
		}
		clearWidgetsMetaTags(widgets);
		screen.load();
	}
	
	/**
	 * 
	 * @param screenElement
	 */
	private void clearScreenMetaTag(Element screenElement)
	{
		while (screenElement.hasChildNodes())
		{
			Node child = screenElement.getFirstChild();
			screenElement.removeChild(child);
			screenElement.getParentNode().insertBefore(child, screenElement);
		}
		Node parent = screenElement.getParentNode();
		if (parent != null)
		{
			parent.removeChild(screenElement);
		}
	}
	
	/**
	 * 
	 * @param widgets
	 */
	private void clearWidgetsMetaTags(List<Element> widgets)
	{
		for (Element element : widgets) 
		{
			String widgetId = element.getId();
			if (widgetId != null && widgetId.length() >= 0)
			{
				element = DOM.getElementById(widgetId); // Evita que elementos reanexados ao DOM sejam esquecidos
				Element parent;
				if (element != null && (parent = element.getParentElement())!= null)
				{
					parent.removeChild(element);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	private Element getParentElement(Element element) 
	{
		Element elementParent = element.getParentElement();
		while (elementParent != null && !"body".equalsIgnoreCase(elementParent.getTagName()))
		{
			if (isValidWidget(elementParent))
			{
				return elementParent;
			}
			elementParent = elementParent.getParentElement();
		}
			
		return null;	
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param widgetsElementsAdded
	 * @return
	 * @throws InterfaceConfigException
	 */
	@SuppressWarnings("unchecked")
	private Widget createWidget(Element element, Screen screen, List<Element> widgetsElementsAdded) throws InterfaceConfigException
	{
		String widgetId = element.getId();
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetIdRequired());
		}
		Widget widget = screen.getWidget(widgetId);
		if (widget != null)
		{
			return widget;
		}
		
		Widget parent = null;
		Element parentElement = getParentElement(element);
		if (parentElement != null)
		{
			parent = screen.getWidget(parentElement.getId());
			if (parent == null)
			{
				parent = createWidget(parentElement, screen, widgetsElementsAdded);
			}
			
			WidgetFactory<?> parentWidgetFactory = registeredWidgetFactories.getWidgetFactory(parentElement.getAttribute("_type"));
			if (parentWidgetFactory instanceof HasWidgetsFactory)
			{
				widget = newWidget(element, widgetId);
				((HasWidgetsFactory<Widget>)parentWidgetFactory).add(parent, widget, parentElement, element);
			}
			else
			{
				widget = screen.getWidget(widgetId);
			}
		}
		else
		{
			widget = newWidget(element, widgetId);

			Element panelElement;
			boolean parentHasMoreThanOneChild = (element.getNextSiblingElement() != null || DOMUtils.getPreviousSiblingElement(element) != null);
			if (Crux.getConfig().wrapSiblingWidgets() && parentHasMoreThanOneChild)
			{
				panelElement = DOM.createSpan();
				element.getParentElement().insertBefore(panelElement, element);
			}
			else
			{
				if (parentHasMoreThanOneChild)
				{
					GWT.log(Crux.getMessages().screenFactoryNonDeterministicWidgetPositionInParent(widgetId), null);
				}
				panelElement = element.getParentElement();
			}

			CruxWidgetPanel panel = new CruxWidgetPanel(panelElement);
			panel.add(widget);
		}

		widgetsElementsAdded.add(element);
		return widget;
	}
}
