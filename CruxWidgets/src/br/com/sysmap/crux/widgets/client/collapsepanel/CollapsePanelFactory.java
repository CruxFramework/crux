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
package br.com.sysmap.crux.widgets.client.collapsepanel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeCollapseEvtBind;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandEvtBind;
import br.com.sysmap.crux.widgets.client.titlepanel.AbstractTitlePanelFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Collapse Panel widget
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="collapsePanel", library="widgets")
public class CollapsePanelFactory extends AbstractTitlePanelFactory<CollapsePanel>
{
	@Override
	public CollapsePanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getAttribute("_height");
		String width = element.getAttribute("_width");
		String styleName = element.getAttribute("_styleName");

		String strCollapsible = element.getAttribute("_collapsible");
		boolean collapsible = true;
		if(strCollapsible != null && strCollapsible.trim().length() > 0)
		{
			collapsible = Boolean.parseBoolean(strCollapsible);
		}
		
		String strCollapsed = element.getAttribute("_collapsed");
		boolean collapsed = false;
		if(strCollapsed != null && strCollapsed.trim().length() > 0)
		{
			collapsed = Boolean.parseBoolean(strCollapsed);
		}
		
		return new CollapsePanel(width, height, styleName, collapsible, collapsed);
	}
	
	@Override
	@TagEvents({
		@TagEvent(BeforeCollapseEvtBind.class),
		@TagEvent(BeforeExpandEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<CollapsePanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="collapsed", type=Boolean.class),
		@TagAttributeDeclaration(value="collapsible", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<CollapsePanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(TitleProcessor.class),
		@TagChild(BodyProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<CollapsePanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="title", minOccurs="0")
	public static class TitleProcessor extends WidgetChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException	{}
	}

	public static class TitleChildrenProcessor extends ChoiceChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(CollapsePanelHTMLChildProcessor.class),
			@TagChild(CollapsePanelTextChildProcessor.class),
			@TagChild(CollapsePanelWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class CollapsePanelWidgetProcessor extends WidgetChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleWidgetTitleProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<CollapsePanel> {}
	
	@TagChildAttributes(tagName="body", minOccurs="0")
	public static class BodyProcessor extends WidgetChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(BodyChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException {}
	}

	public static class BodyChildrenProcessor extends ChoiceChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(CollapsePanelBodyHTMLChildProcessor.class),
			@TagChild(CollapsePanelBodyTextChildProcessor.class),
			@TagChild(CollapsePanelBodyWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class CollapsePanelBodyWidgetProcessor extends WidgetChildProcessor<CollapsePanel>
	{
		@Override
		@TagChildren({
			@TagChild(BodyWidgetContentProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<CollapsePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(widgetProperty="contentWidget")
	public static class BodyWidgetContentProcessor extends AnyWidgetChildProcessor<CollapsePanel> {}

	public static class CollapsePanelHTMLChildProcessor extends HTMLChildProcessor<CollapsePanel>{}
	public static class CollapsePanelTextChildProcessor extends TextChildProcessor<CollapsePanel>{}
	public static class CollapsePanelBodyHTMLChildProcessor extends BodyHTMLChildProcessor<CollapsePanel>{}
	public static class CollapsePanelBodyTextChildProcessor extends BodyTextChildProcessor<CollapsePanel> {}
	
}