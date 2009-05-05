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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Represents a SplitPanel
 * @author Thiago Bustamante
 */
public abstract class SplitPanel extends Panel
{

	protected SplitPanel(String id, com.google.gwt.user.client.ui.Panel widget) 
	{
		super(id, widget);
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		try 
		{
			renderSplitItens(element);
		}
		catch (InterfaceConfigException e) 
		{
			GWT.log(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Render internal components
	 * @param element
	 * @throws InterfaceConfigException
	 */
	protected void renderSplitItens(Element element) throws InterfaceConfigException
	{
		NodeList<Node> itensCandidates = element.getChildNodes();
		for (int i=0; i<itensCandidates.getLength(); i++)
		{
			if (isValidItem(itensCandidates.getItem(i)))
			{
				Element e = (Element)itensCandidates.getItem(i);
				renderSplitItem(e);
			}
		}
	}	
	
	protected abstract void renderSplitItem(Element element) throws InterfaceConfigException;
	
	protected Element getComponentChildElement(Element element)
	{
		NodeList<Node> itensCandidates = element.getChildNodes();
		for (int i = 0; i<itensCandidates.getLength(); i++)
		{
			if (itensCandidates.getItem(i) instanceof Element)
			{
				Element e = (Element)itensCandidates.getItem(i);
				String type = e.getAttribute("_type");
				if (type != null && type.length() > 0 && !type.equals("screen"))
				{
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * Verify if the span tag found is a valid item declaration for splitPanel
	 * @param element
	 * @return
	 */
	protected boolean isValidItem(Node node)
	{
		if (node instanceof Element)
		{
			Element element = (Element)node;
			if ("span".equalsIgnoreCase(element.getTagName()))
			{
				String position = element.getAttribute("_position");
				if (position != null && position.length() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
}
