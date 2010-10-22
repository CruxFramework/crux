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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHTMLPanelFactory<T extends HTMLPanel> extends ComplexPanelFactory<T> implements HasWidgetsFactory<T>
{
	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(T parent, Widget child, Element parentElement, Element childElement) 
	{
		parent.add(child, getEnclosingPanelElement(childElement).getId());
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected FastList<Node> extractChildren(Element element)
	{
		FastList<Node> result = new FastList<Node>();
		
		NodeList<Node> childNodes = element.getChildNodes();
		
		for (int i=0; i< childNodes.getLength(); i++)
		{
			Node node = childNodes.getItem(i);
			result.add(node);
		}

		for (int i = result.size()-1; i>=0; i--)
		{
			Node node = result.get(i);
			if (node.getParentNode() != null)
			{
				node.getParentNode().removeChild(node);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param element
	 * @param acceptsNoChild
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static FastList<Element> ensureChildrenSpans(Element element, boolean acceptsNoChild) throws InterfaceConfigException
	{
		return new FastList<Element>();
	}
	
}
