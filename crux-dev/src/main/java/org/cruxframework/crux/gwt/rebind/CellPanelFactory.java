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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="borderWidth",type=Integer.class),
	@TagAttribute(value="spacing",type=Integer.class)
})
@TagChildren({
	@TagChild(CellPanelFactory.CellPanelProcessor.class)
})		
public abstract class CellPanelFactory <C extends CellPanelContext> extends ComplexPanelFactory<C>
{
	private static final String DEFAULT_V_ALIGN = HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString();
	private static final String DEFAULT_H_ALIGN = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	
	public static class CellPanelProcessor extends AbstractCellPanelProcessor<CellPanelContext>{} 

	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(CellProcessor.class),
		@TagChild(CellWidgetProcessor.class)
	})		
	public static abstract class AbstractCellPanelProcessor<C extends CellPanelContext> extends ChoiceChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
	
	public static class CellProcessor extends AbstractCellProcessor<CellPanelContext>{}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(value=CellWidgetProcessor.class)
	})		
	public static abstract class AbstractCellProcessor<C extends CellPanelContext> extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			context.height = context.readChildProperty("height");
			context.width = context.readChildProperty("width");
			context.horizontalAlignment = context.readChildProperty("horizontalAlignment");
			context.verticalAlignment = context.readChildProperty("verticalAlignment");
		}
	}
	
	public static class CellWidgetProcessor extends AbstractCellWidgetProcessor<CellPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			context.child = child;
			super.processChildren(out, context);
			context.child = null;
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}
	
	@TagConstraints(type=AnyWidget.class)
	static class AbstractCellWidgetProcessor<C extends CellPanelContext> extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
		{
			String parent = context.getWidget();
			if (!StringUtils.isEmpty(context.height))
			{
				out.println(parent+".setCellHeight("+context.child+", "+EscapeUtils.quote(context.height)+");");
			}
			if (!StringUtils.isEmpty(context.horizontalAlignment))
			{
				out.println(parent+".setCellHorizontalAlignment("+context.child+", "+
					  AlignmentAttributeParser.getHorizontalAlignment(context.horizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			if (!StringUtils.isEmpty(context.verticalAlignment))
			{
				out.println(parent+".setCellVerticalAlignment("+context.child+", "+AlignmentAttributeParser.getVerticalAlignment(context.verticalAlignment)+");");
			}
			if (!StringUtils.isEmpty(context.width))
			{
				out.println(parent+".setCellWidth("+context.child+", "+EscapeUtils.quote(context.width)+");");
			}
			
			context.height = null;
			context.width = null;
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
}