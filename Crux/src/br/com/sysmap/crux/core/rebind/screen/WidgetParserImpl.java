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

import java.util.List;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.TextExtractor;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetParserImpl implements WidgetParser
{
	/**
	 * 
	 */
	public void parse(Widget widget, Object element) 
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
		if(elem != null && elem.getName().toUpperCase().equals("SPAN"))
		{
			if(!isWidget(elem) || parseIfWidget)
			{
				extractProperties(elem, widget);
				
				List<?>childElements = elem.getChildElements();
				
				if(childElements != null && childElements.size() > 0)
				{
					for (Object child : childElements)
					{
						if(child instanceof Element)
						{
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
	private void extractInnerText(Element elem, Widget widget)
	{
		TextExtractor textExtractor = elem.getTextExtractor();
		
		if(textExtractor != null)
		{
			String text = textExtractor.toString();
			
			text = text.trim();
			
			if(text.length() > 0)
			{
				widget.addPropertyValue(text);
			}
		}
	}

	/**
	 * 
	 * @param elem
	 * @return
	 */
	private boolean isWidget(Element elem)
	{
		String att = elem.getAttributeValue("_type");
		return att != null && att.trim().length() > 0;
	}

	/**
	 * 
	 * @param elem
	 * @param widget
	 */
	private void extractProperties(Element elem, Widget widget)
	{
		Attributes attrs =  elem.getAttributes();
		
		for (Object object : attrs) 
		{
			Attribute attr = (Attribute)object;
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
