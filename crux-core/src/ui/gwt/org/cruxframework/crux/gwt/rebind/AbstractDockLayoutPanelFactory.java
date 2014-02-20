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
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

class DockLayoutPanelContext extends AbstractLayoutPanelContext
{
	String left;
	String top;
	Direction direction;
	Double size = -1.0;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractDockLayoutPanelFactory<C extends DockLayoutPanelContext> 
	  extends AbstractLayoutPanelFactory<C>
{
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="direction", type=Direction.class, defaultValue="CENTER"),
		@TagAttributeDeclaration(value="size", type=Double.class)
	})
	public static abstract class AbstractDockLayoutPanelProcessor<C extends DockLayoutPanelContext> 
	                       extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			context.direction = getDirection(context.readChildProperty("direction"));
			String sizeStr = context.readChildProperty("size");
			if (StringUtils.isEmpty(sizeStr))
			{
				context.size = -1.0;
			}
			else
			{
				context.size = Double.parseDouble(sizeStr);
			}
		}

		private Direction getDirection(String direction)
		{
			Direction result;
			if (!StringUtils.isEmpty(direction))
			{
				result = Direction.valueOf(direction);
			}
			else
			{
				result = Direction.CENTER;
			}
			return result;
		}
	}
	
	
	@TagConstraints(type=AnyWidget.class)
	public static class AbstractDockPanelWidgetProcessor<C extends DockLayoutPanelContext> extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			
			
			if (!context.direction.equals(Direction.CENTER) && context.size == -1)
			{
				throw new CruxGeneratorException("The attribute size is required for cells not centered in DockLayoutPanel wiht id: ["+context.getWidgetId()+"].");
			}
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			
			if (context.animationDuration > 0)
			{
				processAnimatedChild(context, childWidget, context.direction, context.size);
			}
			else
			{
				out.println(processChild(context, childWidget, context.direction, context.size));
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
		}

		/**
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 */
		protected void processAnimatedChild(C context, String childWidget, Direction direction, double size)
		{
			context.addChildWithAnimation(processChild(context, childWidget, direction, size));
		}

		/**
		 * 
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 */
		protected String processChild(C context, String childWidget, Direction direction, double size)
		{
			String rootWidget = context.getWidget();
			
			String result;
			if (direction.equals(Direction.CENTER))
			{
				result = rootWidget+".add("+childWidget+");";	
			}
			else if (direction.equals(Direction.EAST))
			{
				result = rootWidget+".addEast("+childWidget+", "+size+");";
			}
			else if (direction.equals(Direction.NORTH))
			{
				result = rootWidget+".addNorth("+childWidget+", "+size+");";
			}
			else if (direction.equals(Direction.SOUTH))
			{
				result = rootWidget+".addSouth("+childWidget+", "+size+");";				
			}
			else if (direction.equals(Direction.WEST))
			{
				result = rootWidget+".addWest("+childWidget+", "+size+");";
			}
			else if (direction.equals(Direction.LINE_START))
			{
				result = rootWidget+".addLineStart("+childWidget+", "+size+");";
			}
			else if (direction.equals(Direction.LINE_END))
			{
				result = rootWidget+".addLineEnd("+childWidget+", "+size+");";
			}
			else
			{
				result = "";
			}
			return result;
		}
	}
}
