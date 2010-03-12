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
import com.google.gwt.user.client.ui.DecoratedTabBar;

/**
 * Factory for DecoratedTabBar widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
@DeclarativeFactory(id="decoratedTabBar", library="gwt")
public class DecoratedTabBarFactory extends AbstractTabBarFactory<DecoratedTabBar>
{
	@Override
	public DecoratedTabBar instantiateWidget(Element element, String widgetId) 
	{
		return new DecoratedTabBar();
	}
	
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<DecoratedTabBar> context) throws InterfaceConfigException {}		

	public static class TabProcessor extends AbstractTabProcessor<DecoratedTabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(TabItemProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DecoratedTabBar> context) throws InterfaceConfigException
		{
			super.processChildren(context);
		}
	}
	
	public static class TabItemProcessor extends ChoiceChildProcessor<DecoratedTabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTabProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<DecoratedTabBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetTabProcessor extends WidgetChildProcessor<DecoratedTabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DecoratedTabBar> context) throws InterfaceConfigException {}
	}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor<DecoratedTabBar> {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor<DecoratedTabBar> {}
	
	public static class WidgetProcessor extends AbstractWidgetProcessor<DecoratedTabBar> {}
}
