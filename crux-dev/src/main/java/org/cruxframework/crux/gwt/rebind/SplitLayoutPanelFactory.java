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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

class SplitLayoutPanelContext extends DockLayoutPanelContext
{

	public String minSize;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="splitLayoutPanel", library="gwt", targetWidget=SplitLayoutPanel.class)
@TagChildren({
	@TagChild(SplitLayoutPanelFactory.SplitLayoutPanelProcessor.class)
})		
public class SplitLayoutPanelFactory extends AbstractDockLayoutPanelFactory<SplitLayoutPanelContext>
{
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="minSize", type=Integer.class)
	})
	@TagChildren({
		@TagChild(SplitLayoutPanelWidgetProcessor.class)
	})		
	public static class SplitLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<SplitLayoutPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, SplitLayoutPanelContext context) throws CruxGeneratorException
		{
			context.minSize = context.readChildProperty("minSize");
			super.processChildren(out, context);
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class SplitLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<SplitLayoutPanelContext> 
	{
		/**
		 * @see org.cruxframework.crux.gwt.rebind.AbstractDockLayoutPanelFactory.AbstractDockPanelWidgetProcessor#processAnimatedChild(org.cruxframework.crux.gwt.rebind.DockLayoutPanelContext, java.lang.String, com.google.gwt.user.client.ui.DockLayoutPanel.Direction, double)
		 */
		@Override
		protected void processAnimatedChild(SplitLayoutPanelContext context, String childWidget, Direction direction, double size)
		{
			
			context.addChildWithAnimation(processChild(context, childWidget, direction, size, context.minSize));
		}
		
		/**
		 * @see org.cruxframework.crux.gwt.rebind.AbstractDockLayoutPanelFactory.AbstractDockPanelWidgetProcessor#processChild(org.cruxframework.crux.gwt.rebind.DockLayoutPanelContext, com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.DockLayoutPanel.Direction, double)
		 */
		@Override
		protected String processChild(SplitLayoutPanelContext context, String childWidget, Direction direction, double size)
		{
			return processChild(context, childWidget, direction, size, context.minSize);
		}

		/**
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 * @param minSize
		 */
		protected String processChild(SplitLayoutPanelContext context, String childWidget, Direction direction, double size, String minSize)
		{
			StringBuilder result = new StringBuilder(super.processChild(context, childWidget, direction, size)+"\n");
			if (!StringUtils.isEmpty(minSize))
			{
				result.append(Scheduler.class.getCanonicalName()+".get().scheduleDeferred(new "+ScheduledCommand.class.getCanonicalName()+"(){\n");
				result.append("public void execute(){\n");
				result.append(context.getWidget()+".setWidgetMinSize("+childWidget+", "+Integer.parseInt(minSize)+");\n");
				result.append("}\n");
				result.append("});\n");
			}
			return result.toString();
		}
	}

	@Override
    public SplitLayoutPanelContext instantiateContext()
    {
	    return new SplitLayoutPanelContext();
    }
}
