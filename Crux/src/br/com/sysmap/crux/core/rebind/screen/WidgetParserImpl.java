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
package br.com.sysmap.crux.core.rebind.screen;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetParserImpl implements WidgetParser
{
	/**
	 * 
	 */
	public void parse(Widget widget, Element element) 
	{
		Element elem = (Element) element;
		parse(elem, widget, true);
	}

	/**
	 * 
	 * @param elem
	 * @param widget
	 * @param parseIfWidget
	 */
	private void parse(Element elem, Widget widget, boolean parseIfWidget)
	{
		if(elem != null && elem.getLocalName().equalsIgnoreCase("SPAN"))
		{
			if(!isWidget(elem) || parseIfWidget)
			{
				extractProperties(elem, widget);
				
				NodeList childElements = elem.getChildNodes();
				
				int length = childElements.getLength();
				if(childElements != null && length > 0)
				{
					for (int i = 0; i < length; i++)
					{
						Node childNode = childElements.item(i);
						
						if(Node.TEXT_NODE == childNode.getNodeType())
						{
							extractInnerText(childNode, widget);
						}
						else if(Node.ELEMENT_NODE == childNode.getNodeType())
						{
							Element child = (Element) childElements.item(i);
							Element childElem = (Element) child;
							parse(childElem, widget, false);
						}
					}
				}
				else
				{
					extractInnerText(elem, widget);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param elem
	 * @param widget
	 */
	private void extractInnerText(Node elem, Widget widget)
	{
		String text = elem.getTextContent();
		
		if(text != null && text.trim().length() > 0)
		{
			widget.addPropertyValue(text);
		}
	}

	/**
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isWidget(Element elem)
	{
		String att = elem.getAttribute("_type");
		return att != null && att.trim().length() > 0;
	}

	/**
	 * 
	 * @param elem
	 * @param widget
	 */
	private void extractProperties(Element elem, Widget widget)
	{
		NamedNodeMap attributes = elem.getAttributes();
		
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) 
		{
			Attr attr = (Attr)attributes.item(i);
			String attrName = attr.getName();
			
			if (attrName.equals("id") || attrName.equals("_type"))
			{
				continue;
			}
			
			if (attrName.startsWith("_on"))
			{
				setEvent(widget, attrName, attr.getValue());
			}
			else if (attrName.equals("_formatter"))
			{
				widget.setFormatter(attr.getValue());
			}
			else if (attrName.equals("_datasource"))
			{
				widget.setDataSource(attr.getValue());
			}
			else
			{
				widget.addPropertyValue(attr.getValue());
			}
		}
	}

	/**
	 * 
	 * @param widget
	 * @param evtName
	 * @param value
	 */
	protected void setEvent(Widget widget, String evtName, String value)
	{
		Event event = EventFactory.getEvent(evtName, value);
		
		if (event != null)
		{
			widget.addEvent(event);
		}
	}
	
}
