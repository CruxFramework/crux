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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A LayzyPanel that encapsulate a SimplePanelFactory
 * @author Thiago Bustamante
 */
public class SimpleLazyPanelFactory extends SimplePanelFactory
{
	protected com.google.gwt.user.client.ui.LazyPanel lazyPanelWidget;
	
	@Override
	protected SimplePanel instantiateWidget(Element element, String widgetId) 
	{
		return new LazyPanel()
		{
			@Override
			protected Widget createWidget() 
			{
				return new SimplePanel();
			}
		};
	}
}
