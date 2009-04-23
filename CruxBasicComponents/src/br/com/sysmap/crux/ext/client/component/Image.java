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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Represents an Image component
 * @author Thiago Bustamante
 */
public class Image extends Component
{
	protected com.google.gwt.user.client.ui.Image imageWidget;
	
	public Image(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.Image());
	}

	public Image(String id, com.google.gwt.user.client.ui.Image widget) 
	{
		super(id, widget);
		this.imageWidget = widget;
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		// TODO Auto-generated method stub
		super.renderAttributes(element);
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);

		ClickEvtBind.bindEvent(element, imageWidget, getId());
	}
	
	
}
