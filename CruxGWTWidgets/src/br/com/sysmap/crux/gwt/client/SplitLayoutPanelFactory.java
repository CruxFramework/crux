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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="splitLayoutPanel", library="gwt")
public class SplitLayoutPanelFactory extends AbstractDockLayoutPanelFactory<SplitLayoutPanel>
{
	@Override
	public SplitLayoutPanel instantiateWidget(Element element, String widgetId)
	{
		return new SplitLayoutPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(SplitLayoutPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<SplitLayoutPanel> context) throws InterfaceConfigException {}
	
	public static class SplitLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<SplitLayoutPanel>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="minSize", type=Integer.class)
		})
		@TagChildren({
			@TagChild(SplitLayoutPanelWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<SplitLayoutPanel> context) throws InterfaceConfigException
		{
			context.setAttribute("minSize", context.readChildProperty("minSize"));
			super.processChildren(context);
		}
	}
	
	public static class SplitLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<SplitLayoutPanel> 
	{
		@SuppressWarnings("unchecked")
		@Override
		protected void processAnimatedChild(final WidgetChildProcessorContext<SplitLayoutPanel> context, final Widget childWidget,
				                            final Direction direction, final double size)
		{
			List<Command> animationConstraints = (List<Command>) context.getAttribute("animationCommands");
			final String minSize = (String) context.getAttribute("minSize");
			animationConstraints.add(new Command(){
				public void execute()
				{
					processChild(context, childWidget, direction, size, minSize);
				}
			});
		}
		
		@Override
		protected void processChild(WidgetChildProcessorContext<SplitLayoutPanel> context, Widget childWidget, Direction direction, double size)
		{
			processChild(context, childWidget, direction, size, (String) context.getAttribute("minSize"));
		}

		protected void processChild(final WidgetChildProcessorContext<SplitLayoutPanel> context, final Widget childWidget, Direction direction, double size, final String minSize)
		{
			super.processChild(context, childWidget, direction, size);
			if (!StringUtils.isEmpty(minSize))
			{
				Scheduler.get().scheduleDeferred(new ScheduledCommand(){
					public void execute()
					{
						context.getRootWidget().setWidgetMinSize(childWidget, Integer.parseInt(minSize));
					}
				});
			}
		}
	}
}
