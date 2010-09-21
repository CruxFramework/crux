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

import java.util.List;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractDockLayoutPanelFactory<T extends DockLayoutPanel> extends AbstractLayoutPanelFactory<T>
{
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static abstract class AbstractDockLayoutPanelProcessor<W extends DockLayoutPanel> extends WidgetChildProcessor<W> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="direction", type=Direction.class, defaultValue="CENTER"),
			@TagAttributeDeclaration(value="size", type=Double.class)
		})
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("direction", getDirection(context.readChildProperty("direction")));
			context.setAttribute("size", context.readChildProperty("size"));
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
	
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class AbstractDockPanelWidgetProcessor<W extends DockLayoutPanel> extends WidgetChildProcessor<W> 
	{
		GWTMessages messages = GWT.create(GWTMessages.class);
		
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			Widget childWidget = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			
			Direction direction = (Direction) context.getAttribute("direction");
			String sizeStr = (String) context.getAttribute("size");
			double size = -1;
			if (!StringUtils.isEmpty(sizeStr))
			{
				size = Double.parseDouble(sizeStr);			
			}
			
			if (!direction.equals(Direction.CENTER) && size == -1)
			{
				throw new InterfaceConfigException(messages.dockLayoutPanelRequiredSize(context.getRootWidgetId()));
			}
			
			Integer animationDuration = (Integer) context.getAttribute("animationDuration");
			if (animationDuration != null)
			{
				processAnimatedChild(context, childWidget, direction, size);
			}
			else
			{
				processChild(context, childWidget, direction, size);
			}
		}

		@SuppressWarnings("unchecked")
		protected void processAnimatedChild(final WidgetChildProcessorContext<W> context, final Widget childWidget, final Direction direction, final double size)
		{
			List<Command> animationConstraints = (List<Command>) context.getAttribute("animationCommands");
			animationConstraints.add(new Command(){
				public void execute()
				{
					processChild(context, childWidget, direction, size);
				}
			});
		}

		/**
		 * 
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 */
		protected void processChild(WidgetChildProcessorContext<W> context, Widget childWidget, Direction direction, double size)
		{
			if (direction.equals(Direction.CENTER))
			{
				context.getRootWidget().add(childWidget);	
			}
			else if (direction.equals(Direction.EAST))
			{
				context.getRootWidget().addEast(childWidget, size);
			}
			else if (direction.equals(Direction.NORTH))
			{
				context.getRootWidget().addNorth(childWidget, size);
			}
			else if (direction.equals(Direction.SOUTH))
			{
				context.getRootWidget().addSouth(childWidget, size);				
			}
			else if (direction.equals(Direction.WEST))
			{
				context.getRootWidget().addWest(childWidget, size);
			}
		}
	}
}
