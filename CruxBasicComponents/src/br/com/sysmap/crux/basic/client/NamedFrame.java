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

import br.com.sysmap.crux.core.client.component.Component;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a NamedFrame Component
 * @author Thiago Bustamante
 *
 */
public class NamedFrame extends Frame
{
	protected com.google.gwt.user.client.ui.NamedFrame namedFrameWidget;
	
	public NamedFrame(String id, Element element) 
	{
		this(id, new com.google.gwt.user.client.ui.NamedFrame(element.getAttribute("_name")));
	}

	public NamedFrame(String id, String name) 
	{
		this(id, new com.google.gwt.user.client.ui.NamedFrame(name));
	}

	protected NamedFrame(String id, com.google.gwt.user.client.ui.NamedFrame widget) 
	{
		super(id, widget);
		this.namedFrameWidget = widget;
	}

	/**
	 * Gets the name associated with this frame.
	 * 
	 * @return the frame's name
	 */
	public String getName() 
	{
		return namedFrameWidget.getName();
	}
	
	@Override
	protected Widget getComponentWidget(Component component)
	{
		return super.getComponentWidget(component);
	}
	
}
