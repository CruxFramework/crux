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
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
@DeclarativeFactory(id="tabPanel", library="gwt")
public class TabPanelFactory extends AbstractTabPanelFactory<TabPanel> 
{
	@Override
	public TabPanel instantiateWidget(Element element, String widgetId) 
	{
		return new TabPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})	
	public void processChildren(WidgetFactoryContext<TabPanel> context) throws InterfaceConfigException 
	{
	}
	
	public static class TabProcessor extends AbstractTabProcessor<TabPanel>
	{
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabPanel> context) throws InterfaceConfigException
		{
			super.processChildren(context);
		}
		
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<TabPanel> context) throws InterfaceConfigException {}
		
	}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor<TabPanel> {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor<TabPanel> {}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabPanel> context) throws InterfaceConfigException {}
	}

	public static class WidgetTitleProcessor extends AbstractWidgetTitleProcessor<TabPanel> {}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabPanel> context) throws InterfaceConfigException {}
	}

	public static class WidgetContentProcessor extends AbstractWidgetContentProcessor<TabPanel> {}	
}
