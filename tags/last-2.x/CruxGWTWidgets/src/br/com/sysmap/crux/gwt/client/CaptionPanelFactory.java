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
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CaptionPanel;

/**
 * Factory for CaptionPanel widgets
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="captionPanel", library="gwt")
public class CaptionPanelFactory extends CompositeFactory<CaptionPanel>
{
	@Override
	@TagAttributes({
		@TagAttribute("captionText")
	})
	public void processAttributes(WidgetFactoryContext<CaptionPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CaptionProcessor.class),
		@TagChild(ContentProcessor.class)
	})	
	public void processChildren(WidgetFactoryContext<CaptionPanel> context) throws InterfaceConfigException {}
	
	@Override
	public CaptionPanel instantiateWidget(Element element, String widgetId) 
	{
		return new CaptionPanel();
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class CaptionProcessor extends ChoiceChildProcessor<CaptionPanel>
	{
		@Override
		@TagChildren({
			@TagChild(CaptionTextProcessor.class),
			@TagChild(CaptionHTMLProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<CaptionPanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(minOccurs="0", tagName="widget")
	public static class ContentProcessor extends WidgetChildProcessor<CaptionPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<CaptionPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(minOccurs="0", widgetProperty="contentWidget")
	public static class WidgetProcessor extends AnyWidgetChildProcessor<CaptionPanel> {}
	
	@TagChildAttributes(tagName="captionText", type=String.class)
	public static class CaptionTextProcessor extends WidgetChildProcessor<CaptionPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<CaptionPanel> context) throws InterfaceConfigException 
		{
			context.getRootWidget().setCaptionText(context.getChildElement().getInnerHTML());
		}
	}
	
	@TagChildAttributes(tagName="captionHTML", type=AnyTag.class)
	public static class CaptionHTMLProcessor extends WidgetChildProcessor<CaptionPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<CaptionPanel> context) throws InterfaceConfigException 
		{
			context.getRootWidget().setCaptionHTML(context.getChildElement().getInnerHTML());
		}
	}
}
