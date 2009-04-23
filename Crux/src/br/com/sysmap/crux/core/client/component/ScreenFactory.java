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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.JSEngine;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.RegisteredClientFormatters;
import br.com.sysmap.crux.core.client.utils.DOMUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Factory for CRUX screen. Based in the componentName (extracted form class attribute), 
 * determine witch class to create for all screen components.
 * @author Thiago
 *
 */
public class ScreenFactory {
	
	private static ScreenFactory instance = null;
	 
	private Screen screen = null;
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredComponents registeredComponents = null;
	private String screenId = null;
	
	private ScreenFactory()
	{
		this.registeredComponents = (RegisteredComponents) GWT.create(RegisteredComponents.class);
		this.registeredClientFormatters = (RegisteredClientFormatters) GWT.create(RegisteredClientFormatters.class);
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
			screen = create();
		}
		return screen;
	}
	
	private Screen create()
	{
		Screen screen = new Screen(getScreenId());
		Element body = RootPanel.getBodyElement();
		NodeList<Element> componentCandidates = body.getElementsByTagName("span");
		List<Element> components = new ArrayList<Element>();
		Element screenElement = null;
		
		for (int i=0; i<componentCandidates.getLength(); i++)
		{
			if (isValidComponent(componentCandidates.getItem(i)))
			{
				try 
				{
					createComponent(componentCandidates.getItem(i), screen);
					components.add(componentCandidates.getItem(i));
				}
				catch (InterfaceConfigException e) 
				{
					GWT.log(e.getLocalizedMessage(), e);
					componentCandidates.getItem(i).setInnerText(JSEngine.messages.screenFactoryGenericErrorCreateComponent(e.getLocalizedMessage()));
				}
			}
			else if (isScreenDefinitions(componentCandidates.getItem(i)))
			{
				screenElement = componentCandidates.getItem(i);
				screen.parse(screenElement);
			}
		}
		if (screenElement != null)
		{
			clearScreenMetaTag(screenElement);
		}
		clearComponentsMetaTags(components);
		screen.fireLoadEvent();
		return screen;
	}
	
	private void clearScreenMetaTag(Element screenElement)
	{
		while (screenElement.hasChildNodes())
		{
			Node child = screenElement.getFirstChild();
			screenElement.removeChild(child);
			screenElement.getParentNode().appendChild(child);
		}
		Node parent = screenElement.getParentNode();
		if (parent != null)
		{
			parent.removeChild(screenElement);
		}
	}
	
	private void clearComponentsMetaTags(List<Element> components)
	{
		for (Element element : components) 
		{
			String componentId = element.getAttribute("id");
			if (componentId != null && componentId.trim().length() >= 0)
			{
				element = DOM.getElementById(componentId); // Evita que elementos reanexados ao DOM sejam esquecidos
				Element parent;
				if (element != null && (parent = element.getParentElement())!= null)
				{
					parent.removeChild(element);
				}
			}
		}
	}
	
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

	private Component newComponent(Element element, String componentId) throws InterfaceConfigException
	{
		Component component = registeredComponents.createComponent(componentId, element);
		return component;
	}
	
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

	private boolean isValidComponent(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String type = element.getAttribute("_type");
			if (type != null && type.trim().length() > 0 && !"screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}
	
	private Container getParent(Element element, Screen screen) throws InterfaceConfigException
	{
		Element elementParent = element.getParentElement();
		while (elementParent != null && !"body".equalsIgnoreCase(elementParent.getTagName()))
		{
			if (isValidComponent(elementParent))
			{
				String id = elementParent.getAttribute("id");
				Component parent = screen.getComponent(id); 
					
				if (!(parent instanceof Container))
				{
					throw new InterfaceConfigException(JSEngine.messages.screenFactoryInvalidComponentParent(element.getAttribute("id")));
				}
				return (Container)parent;
			}
			elementParent = elementParent.getParentElement();
		}
			
		return null;	
	}
	
	private Component createComponent(Element element, Screen screen) throws InterfaceConfigException
	{
		String componentId = element.getAttribute("id");
		if (componentId == null || componentId.trim().length() == 0)
		{
			throw new InterfaceConfigException(JSEngine.messages.screenFactoryComponentIdRequired());
		}
		Component component = screen.getComponent(componentId);
		if (component != null)
		{
			return component;
		}
		
		Container parent = getParent(element, screen);
		
		component = newComponent(element, componentId);
		if (component == null)
		{
			throw new InterfaceConfigException(JSEngine.messages.screenFactoryErrorCreateComponent(componentId));
		}
		screen.addComponent(component);
		component.render(element);

		if (parent != null)
		{
			parent.addComponent(component);
		}
		else
		{
			ComponentPanel panel = new ComponentPanel(element.getParentElement());
			panel.add(component.widget);
		}
		return component;
	}
	
	public Formatter getClientFormatter(String formatter)
	{
		return this.registeredClientFormatters.getClientFormatter(formatter);
	}
}
