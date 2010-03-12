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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabBar;

/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
@DeclarativeFactory(id="tabBar", library="gwt")
public class TabBarFactory extends AbstractTabBarFactory<TabBar>
{
	@Override
	public TabBar instantiateWidget(Element element, String widgetId) 
	{
		return new TabBar();
	}
	
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<TabBar> context) throws InterfaceConfigException {}		

	public static class TabProcessor extends AbstractTabProcessor<TabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(TabItemProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException
		{
			super.processChildren(context);
		}
	}
	
	public static class TabItemProcessor extends ChoiceChildProcessor<TabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTabProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetTabProcessor extends WidgetChildProcessor<TabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException {}
	}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor<TabBar> {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor<TabBar> {}
	
	public static class WidgetProcessor extends AbstractWidgetProcessor<TabBar> {}
}
