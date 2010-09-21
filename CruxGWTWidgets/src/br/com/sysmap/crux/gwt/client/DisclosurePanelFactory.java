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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasOpenHandlersFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;

/**
 * Factory for DisclosurePanel widgets
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="disclosurePanel", library="gwt")
public class DisclosurePanelFactory extends CompositeFactory<DisclosurePanel> 
       implements HasAnimationFactory<DisclosurePanel>, 
                  HasOpenHandlersFactory<DisclosurePanel>, HasCloseHandlersFactory<DisclosurePanel>
{
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	
	@Override
	public DisclosurePanel instantiateWidget(Element element, String widgetId) 
	{
		String headerText = getProperty(element,"headerText");
		return new DisclosurePanel(headerText);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="open", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("headerText")
	})
	public void processAttributes(WidgetFactoryContext<DisclosurePanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	@TagChildren({
		@TagChild(HeaderProcessor.class),
		@TagChild(ContentProcessor.class)
	})	
	public void processChildren(WidgetFactoryContext<DisclosurePanel> context) throws InterfaceConfigException {}

	@TagChildAttributes(minOccurs="0", tagName="widgetHeader")
	public static class HeaderProcessor extends WidgetChildProcessor<DisclosurePanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetHeaderProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DisclosurePanel> context) throws InterfaceConfigException {}
	}
		
	@TagChildAttributes(minOccurs="0", tagName="widgetContent")
	public static class ContentProcessor extends WidgetChildProcessor<DisclosurePanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<DisclosurePanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(widgetProperty="content")
	public static class WidgetProcessor extends AnyWidgetChildProcessor<DisclosurePanel> {}
	
	@TagChildAttributes(widgetProperty="header")
	public static class WidgetHeaderProcessor extends AnyWidgetChildProcessor<DisclosurePanel> {}
}
