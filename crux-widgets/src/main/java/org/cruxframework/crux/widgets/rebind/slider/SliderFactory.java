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

@DeclarativeFactory(library="widgets", id="slider", targetWidget=Slider.class)

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
