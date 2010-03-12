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
import com.google.gwt.user.client.ui.DecoratedStackPanel;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="decoratedStackPanel", library="gwt")
public class DecoratedStackPanelFactory extends AbstractStackPanelFactory<DecoratedStackPanel>
{

	@Override
	public DecoratedStackPanel instantiateWidget(Element element, String widgetId) 
	{
		return new DecoratedStackPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(StackItemProcessor.class)
	})	
	public void processChildren(WidgetFactoryContext<DecoratedStackPanel> context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="stackItem")
	public static class StackItemProcessor extends WidgetChildProcessor<DecoratedStackPanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleProcessor.class),
			@TagChild(ContentProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DecoratedStackPanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class TitleProcessor extends ChoiceChildProcessor<DecoratedStackPanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleTextProcessor.class),
			@TagChild(TitleHTMLProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DecoratedStackPanel> context) throws InterfaceConfigException {}
	}

	public static class TitleTextProcessor extends AbstractTitleTextProcessor<DecoratedStackPanel> {}
	
	public static class TitleHTMLProcessor extends AbstractTitleHTMLProcessor<DecoratedStackPanel> {}
	
	@TagChildAttributes(minOccurs="0", tagName="widget")
	public static class ContentProcessor extends WidgetChildProcessor<DecoratedStackPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(ContentWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DecoratedStackPanel> context) throws InterfaceConfigException {}
	}
	
	public static class ContentWidgetProcessor extends AbstractContentWidgetProcessor<DecoratedStackPanel> {}	
}
