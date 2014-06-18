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
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasVerticalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.DockPanelContext.DockDirection;
import org.json.JSONObject;

import com.google.gwt.user.client.ui.DockPanel;

class DockPanelContext extends CellPanelContext
{
	public static enum DockDirection{center, lineStart, lineEnd, east, north, south, west}
	public DockDirection direction;
	
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dockPanel", library="gwt", targetWidget=DockPanel.class)
@TagChildren({
	@TagChild(DockPanelFactory.DockPanelProcessor.class)
})		
public class DockPanelFactory extends CellPanelFactory<DockPanelContext> 
       implements HasHorizontalAlignmentFactory<DockPanelContext>, 
                  HasVerticalAlignmentFactory<DockPanelContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(DockCellProcessor.class),
		@TagChild(DockWidgetProcessor.class)
	})		
	public static class DockPanelProcessor extends AbstractCellPanelProcessor<DockPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
			context.direction = DockDirection.center;
		}
	}
	
	@TagConstraints(tagName="cell", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="direction", type=DockDirection.class, defaultValue="center")
	})
	@TagChildren({
		@TagChild(value=DockWidgetProcessor.class)
	})		
	public static class DockCellProcessor extends AbstractCellProcessor<DockPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);

			String directionProp = context.readChildProperty("direction");
			if (!StringUtils.isEmpty(directionProp))
			{
				try
				{
					context.direction = DockDirection.valueOf(directionProp);
				}
				catch (Exception e) 
				{
					String childId = context.readChildProperty("id");
					throw new CruxGeneratorException("Error adding widget ["+childId+"] on parent Dockpanel ["+context.getWidgetId()+"]. Invalid direction.");
				}
			}
			else
			{
				context.direction = DockDirection.center;
			}
		}
	}

	@TagConstraints(type=AnyWidget.class)
	public static class DockWidgetProcessor extends AbstractCellWidgetProcessor<DockPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException
		{
			JSONObject childElement = context.getChildElement();
			String child = getWidgetCreator().createChildWidget(out, childElement, context);
			String parent = context.getWidget();
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			switch (context.direction) {
				case center: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".CENTER);");
				break;
				case lineStart: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".LINE_START);");
				break;
				case lineEnd: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".LINE_END);");
				break;
				case east: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".EAST);");
				break;
				case north: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".NORTH);");
				break;
				case south: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".SOUTH);");
				break;
				case west: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".WEST);");
				break;

			default:
				break;
			}
			context.direction = DockDirection.center;
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
    public DockPanelContext instantiateContext()
    {
	    return new DockPanelContext();
    }
}
