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
package br.com.sysmap.crux.advanced.client.scrollbanner;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Scroll Banner widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="scrollBanner", library="adv")
public class ScrollBannerFactory extends WidgetFactory<ScrollBanner>
{
	@Override
	public ScrollBanner instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String period = element.getAttribute("_messageScrollingPeriod");
		if(period != null && period.trim().length() > 0)
		{
			return new ScrollBanner(Integer.parseInt(period));
		}
		
		return new ScrollBanner();
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("messageScrollingPeriod")
	})
	public void processAttributes(WidgetFactoryContext<ScrollBanner> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(MessageProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<ScrollBanner> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="message", minOccurs="0", maxOccurs="unbounded", type=String.class)
	public static class MessageProcessor extends WidgetChildProcessor<ScrollBanner>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<ScrollBanner> context) throws InterfaceConfigException
		{
			String message = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getInnerHTML());
			context.getRootWidget().addMessage(message);
		}
	}
}