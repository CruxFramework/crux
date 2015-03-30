/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.rebind.slider;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.widgets.client.slider.Slider;

@DeclarativeFactory(library="widgets", id="slider", targetWidget=Slider.class, 
	description="a slider that shows various widgets, allowing touch slides between them.")

@TagAttributes({
	@TagAttribute(value="circularShowing", type=Boolean.class, defaultValue="false"),
	@TagAttribute(value="slideTransitionDuration", type=Integer.class, defaultValue="500"),
	@TagAttribute(value="showFirstWidget", type=Boolean.class, defaultValue="true", processor=SliderFactory.ShowFirstWidgetProcessor.class)
})
@TagChildren({
	@TagChild(SliderFactory.FieldWidgetChildProcessor.class)
})
public class SliderFactory extends WidgetCreator<WidgetCreatorContext>
{
	public static class ShowFirstWidgetProcessor extends AttributeProcessor<WidgetCreatorContext> 
	{
		public ShowFirstWidgetProcessor(WidgetCreator<?> widgetCreator) 
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue) 
		{
			if(Boolean.valueOf(attributeValue))
			{
				out.println("com.google.gwt.core.client.Scheduler.get().scheduleDeferred(new com.google.gwt.core.client.Scheduler.ScheduledCommand() { @Override public void execute() {" + 
			        	 context.getWidget() + ".showFirstWidget();"
			    + "} });");
			}
		}
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="widget")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})
	public static class FieldWidgetChildProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
