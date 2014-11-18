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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.AbsolutePanel;


class AbsolutePanelContext extends WidgetCreatorContext
{
	String left;
	String top;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="absolutePanel", library="gwt", targetWidget=AbsolutePanel.class)
@TagChildren({
	@TagChild(AbsolutePanelFactory.AbsoluteChildrenProcessor.class)
})	
public class AbsolutePanelFactory extends ComplexPanelFactory<AbsolutePanelContext>
{

	@Override
    public AbsolutePanelContext instantiateContext()
    {
	    return new AbsolutePanelContext();
    }	
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="widget" )
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("left"),
		@TagAttributeDeclaration("top")
	})
	@TagChildren({
		@TagChild(AbsoluteWidgetProcessor.class)
	})	
	public static class AbsoluteChildrenProcessor extends WidgetChildProcessor<AbsolutePanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, AbsolutePanelContext context) throws CruxGeneratorException
		{
			context.left = context.readChildProperty("left");
			context.top = context.readChildProperty("top");
		}
	}
	
	@TagConstraints(type=AnyWidget.class)
	public static class AbsoluteWidgetProcessor extends WidgetChildProcessor<AbsolutePanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, AbsolutePanelContext context) throws CruxGeneratorException
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String absolutePanel = context.getWidget();
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			if (!StringUtils.isEmpty(context.left) && !StringUtils.isEmpty(context.top))
			{
				out.println(absolutePanel+".add("+child+", "+Integer.parseInt(context.left)+", "+Integer.parseInt(context.top)+");");
			}
			else
			{
				out.println(absolutePanel+".add("+child+");");
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
			
			context.left = null;
			context.top = null;
		}
	}
}
