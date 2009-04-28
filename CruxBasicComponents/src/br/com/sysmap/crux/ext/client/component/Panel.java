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

import br.com.sysmap.crux.core.client.component.Container;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for all panels
 * @author Thiago Bustamante
 */
public class Panel extends Container
{
	protected com.google.gwt.user.client.ui.Panel panelWidget;
	
	protected Panel(String id, com.google.gwt.user.client.ui.Panel widget) 
	{
		super(id, widget);
		this.panelWidget = widget;
	}
	
	@Override
	protected void addWidget(Widget widget)
	{
		this.panelWidget.add(widget);	
	}
	
	@Override
	protected void removeWidget(Widget widget)
	{
		this.panelWidget.remove(widget);	
	}

	@Override
	protected void clearWidgetChildren(Widget widget)
	{
		this.panelWidget.clear();
	}
	

}
