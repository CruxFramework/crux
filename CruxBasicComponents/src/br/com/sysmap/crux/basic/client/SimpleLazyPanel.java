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

import com.google.gwt.user.client.ui.Widget;

import br.com.sysmap.crux.core.client.component.Component;

/**
 * A LayzyPanel that encapsulate a SimplePanel
 * @author Thiago Bustamante
 */
public class SimpleLazyPanel extends LazyPanel
{
	protected com.google.gwt.user.client.ui.LazyPanel lazyPanelWidget;
	
	public SimpleLazyPanel(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.LazyPanel()
		{
			@Override
			protected Widget createWidget() 
			{
				return new com.google.gwt.user.client.ui.SimplePanel();
			}
		});
	}

	protected SimpleLazyPanel(String id, com.google.gwt.user.client.ui.LazyPanel widget)
	{
		super(id, widget);
		this.lazyPanelWidget = widget;
	}
	
	@Override
	protected Component createComponent() 
	{
		return new SimplePanel(getId()+"_component", lazyPanelWidget);
	}
}
