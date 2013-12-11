package org.cruxframework.crux.widgets.rebind.image;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadErrorEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.image.Image;
import org.cruxframework.crux.widgets.rebind.event.SelectEvtBind;

import com.google.gwt.resources.client.ImageResource;

/**
 *Factory for Image Widgets
 * @authorThiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="widgets", id="image", targetWidget=Image.class)

@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false"),
	@TagAttribute(value="url", processor=ImageFactory.URLAttributeParser.class, supportsResources=true),
	@TagAttribute(value="altText"),
	@TagAttribute(value="visibleRect", processor=ImageFactory.VisibleRectAttributeParser.class)
})	
@TagEvents({
	@TagEvent(LoadEvtBind.class),
	@TagEvent(LoadErrorEvtBind.class),
	@TagEvent(SelectEvtBind.class)
})
public class ImageFactory extends WidgetCreator<WidgetCreatorContext>
						implements HasAllFocusHandlersFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>
{
	public static class URLAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public URLAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, final WidgetCreatorContext context, String attributeValue)
        {
			String property = context.readWidgetProperty("url");
	        if (getWidgetCreator().isResourceReference(property))
	        {
	        	String resource = ViewFactoryCreator.createVariableName("resource");
	        	out.println("final " + ImageResource.class.getCanonicalName()+" "+resource+" = "+getWidgetCreator().getResourceAccessExpression(property)+";");
	        	out.println("com.google.gwt.core.client.Scheduler.get().scheduleDeferred(new com.google.gwt.core.client.Scheduler.ScheduledCommand() { @Override public void execute() {" 
	        	+ context.getWidget() + ".setUrlAndVisibleRect(Screen.rewriteUrl("+
	        			resource + ".getSafeUri().asString()), "+resource+".getLeft(), "+resource+".getTop(), "+resource+".getWidth(), "+resource+".getHeight()); } });");
	        }
	        else
	        {
	        	out.println(context.getWidget()+".setUrl(Screen.rewriteUrl("+EscapeUtils.quote(property)+"));");
	        }
        }
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleRectAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public VisibleRectAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String widget = context.getWidget();
			String[] coord = attributeValue.split(",");
			
			if (coord != null && coord.length == 4)
			{
				out.println(widget+".setVisibleRect("+Integer.parseInt(coord[0].trim())+", "+Integer.parseInt(coord[1].trim())+","+ 
						Integer.parseInt(coord[2].trim())+", "+Integer.parseInt(coord[3].trim())+");");
			}
        }
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
