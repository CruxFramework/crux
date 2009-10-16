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
package br.com.sysmap.crux.core.client.screen.children;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetChildProcessorContext<W>
{
	private W rootWidget;
	private Element rootElement;
	private String rootWidgetId;
	private Element childElement;
	private Map<String, Object> attributes;
	
	public WidgetChildProcessorContext(W rootWidget, Element rootElement, String rootWidgetId)
	{
		this.rootWidget = rootWidget;
		this.rootElement = rootElement;
		this.rootWidgetId = rootWidgetId;
		this.attributes = new HashMap<String, Object>();
	}

	public W getRootWidget()
	{
		return rootWidget;
	}

	public Element getRootElement()
	{
		return rootElement;
	}

	public String getRootWidgetId()
	{
		return rootWidgetId;
	}

	public Element getChildElement()
	{
		return childElement;
	}
	
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public void setChildElement(Element childElement)
	{
		this.childElement = childElement;
	}
	
	public void setAttribute(String key, Object value)
	{
		this.attributes.put(key, value);
	}
	
	public void clearAttributes()
	{
		this.attributes.clear();
	}
}
