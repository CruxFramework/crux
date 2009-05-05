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

/**
 * Represents a LazyPanel
 * @author Thiago Bustamante
 */
public abstract class LazyPanel extends SimplePanel 
{
	protected com.google.gwt.user.client.ui.LazyPanel lazyPanelWidget;
	
	protected LazyPanel(String id, com.google.gwt.user.client.ui.LazyPanel widget) 
	{
		super(id, widget);
	}

	protected abstract Component createComponent();
	
	public void ensureComponent() 
	{
		lazyPanelWidget.ensureWidget();
	}
}
