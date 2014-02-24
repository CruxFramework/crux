package org.cruxframework.crux.widgets.rebind.slider;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.widgets.client.slider.Slider;

@DeclarativeFactory(library="widgets", id="slider", targetWidget=Slider.class)
@TagChildren({
	@TagChild(SliderFactory.FieldWidgetChildProcessor.class)
})
public class SliderFactory extends WidgetCreator<WidgetCreatorContext>
{
	
	@TagConstraints(minOccurs="0", tagName="widget")
 	@TagChildren({
 		@TagChild(WidgetProcessor.class)
 	})
    public static class FieldWidgetChildProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

    @TagConstraints(minOccurs="0", widgetProperty="widget")
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
