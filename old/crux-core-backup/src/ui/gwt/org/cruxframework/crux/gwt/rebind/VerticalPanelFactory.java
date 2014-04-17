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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasVerticalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="verticalPanel", library="gwt", targetWidget=VerticalPanel.class)
@TagChildren({
	@TagChild(VerticalPanelFactory.VerticalPanelProcessor.class)
})		
public class VerticalPanelFactory extends CellPanelFactory<CellPanelContext>
	   implements HasHorizontalAlignmentFactory<CellPanelContext>, 
	   			  HasVerticalAlignmentFactory<CellPanelContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(VerticalProcessor.class),
		@TagChild(VerticalWidgetProcessor.class)
	})		
	public static class  VerticalPanelProcessor extends AbstractCellPanelProcessor<CellPanelContext> {}
	
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(value=VerticalWidgetProcessor.class)
	})		
	public static class VerticalProcessor extends AbstractCellProcessor<CellPanelContext> {}
		
	public static class VerticalWidgetProcessor extends AbstractCellWidgetProcessor<CellPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String rootWidget = context.getWidget();
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			out.println(rootWidget+".add("+child+");");
			context.child = child;
			super.processChildren(out, context);
			context.child = null;
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}

	@Override
    public CellPanelContext instantiateContext()
    {
	    return new CellPanelContext();
    }	
}
