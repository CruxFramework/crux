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

import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetChildProcessorContext<W>
{
	private Element childElement;
	
	private WidgetFactoryContext<W> context;
	
	public WidgetChildProcessorContext(WidgetFactoryContext<W> context)
	{
		this.context = context;
	}

	public W getRootWidget()
	{
		return context.getWidget();
	}

	public Element getRootElement()
	{
		return context.getElement();
	}

	public String getRootWidgetId()
	{
		return context.getWidgetId();
	}

	public Element getChildElement()
	{
		return childElement;
	}
	
	public Object getAttribute(String key)
	{
		return context.getAttribute(key);
	}

	public void setChildElement(Element childElement)
	{
		this.childElement = childElement;
	}
	
	public void setAttribute(String key, Object value)
	{
		this.context.setAttribute(key, value);
	}
	
	public void clearAttributes()
	{
		this.context.clearAttributes();
	}

	public void removeAttribute(String key)
	{
		this.context.removeAttribute(key);
	}
}
