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

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractStackPanelFactory<T extends StackPanel> extends ComplexPanelFactory<T>
{
	private static final String KEY_IS_HTML = "isHtml";
	private static final String KEY_TITLE = "title";

	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="visibleStack", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		final T widget = context.getWidget();

		final String visibleStack = element.getAttribute("_visibleStack");
		if (visibleStack != null && visibleStack.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.showStack(Integer.parseInt(visibleStack));
				}
			});
		}
	}

	@TagChildAttributes(tagName="textTitle", type=String.class)
	public abstract static class AbstractTitleTextProcessor<T extends StackPanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			context.setAttribute(KEY_TITLE, context.getChildElement().getInnerHTML());
			context.setAttribute(KEY_IS_HTML, false);
		}
	}
	
	@TagChildAttributes(tagName="htmlTitle", type=AnyTag.class)
	public abstract static class AbstractTitleHTMLProcessor<T extends StackPanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			context.setAttribute(KEY_TITLE, context.getChildElement().getInnerHTML());
			context.setAttribute(KEY_IS_HTML, true);
		}
	}
	
	@TagChildAttributes(minOccurs="0", type=AnyWidget.class)
	public abstract static class AbstractContentWidgetProcessor<T extends StackPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			Widget child = createChildWidget(childElement, childElement.getId());
			
			String title = (String)context.getAttribute(KEY_TITLE);
			if (title == null)
			{
				context.getRootWidget().add(child);
			}
			else
			{
				Boolean isHtml = (Boolean)context.getAttribute(KEY_IS_HTML);
				if (isHtml == null)
				{
					context.getRootWidget().add(child, title);
				}
				else
				{
					context.getRootWidget().add(child, title, isHtml);
				}
			}
			context.setAttribute(KEY_TITLE, null);
			context.setAttribute(KEY_IS_HTML, null);
		}	
	}
}
