/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.rebind.scrollbanner;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.widgets.client.scrollbanner.ScrollBanner;


/**
 * Factory for Scroll Banner widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="scrollBanner", library="widgets", targetWidget=ScrollBanner.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration("messageScrollingPeriod")
})
@TagChildren({
	@TagChild(ScrollBannerFactory.MessageProcessor.class)
})
public class ScrollBannerFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();

		String period = context.readWidgetProperty("messageScrollingPeriod");
		if(period != null && period.trim().length() > 0)
		{
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+Integer.parseInt(period)+");");
		}
		else
		{
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"();");
		}
	}

	@TagConstraints(tagName="message", minOccurs="0", maxOccurs="unbounded", type=String.class)
	public static class MessageProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String message = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			String rootWidget = context.getWidget();
			out.println(rootWidget+".addMessage("+message+");");
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}