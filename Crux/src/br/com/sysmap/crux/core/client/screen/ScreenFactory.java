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
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
	private DeclaredI18NMessages declaredI18NMessages = null;
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredDataSources registeredDataSources = null;
	private RegisteredWidgetFactories registeredWidgetFactories = null;
	private Screen screen = null;
	
	private String screenId = null;
	
	private ScreenFactory()
	{
		this.declaredI18NMessages = GWT.create(DeclaredI18NMessages.class);
		this.registeredDataSources = GWT.create(RegisteredDataSources.class);
	}
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	public DataSource<?> createDataSource(String dataSource)
	{
		return this.registeredDataSources.getDataSource(dataSource);
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
	 * @deprecated - Use createDataSource(java.lang.String) instead.
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	public DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
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
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen()
	{
		if (screen == null)
		{
			this.registeredWidgetFactories = (RegisteredWidgetFactories) GWT.create(RegisteredWidgetFactories.class);
			create();
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
	 * Creates a new widget based on a HTML SPAN tag and attaches it on the Screen object.
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return newWidget(element, widgetId, true);
	}
	
	/**
	 * Creates a new widget based on a HTML SPAN tag 
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(Element element, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		String type = element.getAttribute("_type");
		WidgetFactory<? extends Widget> widgetFactory = registeredWidgetFactories.getWidgetFactory(type);
		if (widgetFactory == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetFactoryNotFound(type));
		}
		
		Widget widget = widgetFactory.createWidget(element, widgetId, addToScreen); 
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
		String tagName = element.getTagName();
		if ("span".equalsIgnoreCase(tagName))
		{
			String type = element.getAttribute("_type");
			if (type != null && type.length() > 0 && !"screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}

	void parseDocument()
	{
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
			if (element != null) // Some elements can be handled by its parent's factory
			{
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
		}
		if (screenElement != null)
		{
			clearScreenMetaTag(screenElement);
		}
		clearWidgetsMetaTags(widgets);
		if (Crux.getConfig().renderWidgetsWithIDs())
		{
			screen.updateWidgetsIds();
		}
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
	 */
	private void create()
	{
		screen = new Screen(getScreenId());
		parseDocument();
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param widgetsElementsAdded
	 * @return
	 * @throws InterfaceConfigException
	 */
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
		DeclarativeWidgetFactory widgetFactory = (DeclarativeWidgetFactory) registeredWidgetFactories.getWidgetFactory(element.getAttribute("_type"));
		if (!widgetFactory.isAttachToDOM())
		{
			widget = newWidget(element, widgetId);;
		}
		else
		{
			Element parentElement = getParentElement(element);
			if (parentElement != null)
			{
				widget = createWidgetWithExplicitParent(element, parentElement, screen, widgetsElementsAdded, widgetId);
			}
			else
			{
				widget = createWidgetWithoutExplicitParent(element, parentElement, widgetId);
			}
		}
		if (widget != null)
		{
			widgetsElementsAdded.add(element);
		}
		return widget;
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param widgetsElementsAdded
	 * @param widgetId
	 * @param parentElement
	 * @return
	 * @throws InterfaceConfigException
	 */
	@SuppressWarnings("unchecked")
	private Widget createWidgetWithExplicitParent(Element element, Element parentElement, Screen screen, List<Element> widgetsElementsAdded, String widgetId) throws InterfaceConfigException
	{
		Widget widget;
		Widget parent = screen.getWidget(parentElement.getId());
		if (parent == null)
		{
			String hasWidgetParentId = HasWidgetsHandler.getHasWidgetsId(parentElement);
			if (hasWidgetParentId != null && hasWidgetParentId.length() > 0)
			{
				parent = screen.getWidget(hasWidgetParentId);
			}
			if (parent == null)
			{
				parent = createWidget(parentElement, screen, widgetsElementsAdded);
			}
		}
		
		WidgetFactory<?> parentWidgetFactory = registeredWidgetFactories.getWidgetFactory(parentElement.getAttribute("_type"));
		if (parentWidgetFactory instanceof HasWidgetsFactory)
		{
			widget = newWidget(element, widgetId);
			((HasWidgetsFactory<Widget>)parentWidgetFactory).add(parent, widget, parentElement, element);
		}
		else if (parentWidgetFactory instanceof LayFactory)
		{
			widget = null;
		}
		else
		{
			widget = screen.getWidget(widgetId);
		}
		return widget;
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createWidgetWithoutExplicitParent(Element element, Element parentElement, String widgetId) throws InterfaceConfigException
	{
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
		Widget widget = newWidget(element, widgetId);

		Panel panel;
		if (widget instanceof RequiresResize)
		{
			boolean hasSize = (WidgetFactory.hasWidth(element) && WidgetFactory.hasHeight(element));
			if (RootPanel.getBodyElement().equals(element.getParentElement()) && !hasSize)
			{
				panel = RootLayoutPanel.get();
			}
			else
			{
				ensureElementIdExists(panelElement);
				panel = RootPanel.get(panelElement.getId());
				if (!hasSize)
				{
					GWT.log(Crux.getMessages().screenFactoryLayoutPanelWithoutSize(widgetId), null);
				}
			}
		}
		else
		{
			if (!StringUtils.isEmpty(panelElement.getId()) && Screen.get(panelElement.getId()) != null)
			{
				panel = (Panel) Screen.get(panelElement.getId());
			}
			else
			{
				ensureElementIdExists(panelElement);
				panel = RootPanel.get(panelElement.getId());
			}
		}
		panel.add(widget);
		return widget;
	}

	/**
	 * @param panelElement
	 */
	private void ensureElementIdExists(Element panelElement)
	{
		if (StringUtils.isEmpty(panelElement.getId()))
		{
			panelElement.setId(WidgetFactory.generateNewId());
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
			if (isValidWidget(elementParent) || HasWidgetsHandler.isValidHasWidgetsPanel(elementParent))
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
}
