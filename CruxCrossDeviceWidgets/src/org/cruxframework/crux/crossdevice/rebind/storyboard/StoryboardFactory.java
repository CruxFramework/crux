package org.cruxframework.crux.crossdevice.rebind.storyboard;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.crossdevice.client.storyboard.Storyboard;
import org.cruxframework.crux.gwt.rebind.ComplexPanelFactory;

@DeclarativeFactory(library="crossDevice", id="storyboard", targetWidget=Storyboard.class)
@TagChildren({
	@TagChild(StoryboardFactory.WidgetContentProcessor.class)
})
@TagAttributes({
	@TagAttribute(value="largeDeviceItemWidth", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="smallDeviceItemHeight", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch}),
	@TagAttribute(value="largeDeviceItemHeight", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch})
})
public class StoryboardFactory  extends ComplexPanelFactory<WidgetCreatorContext> implements HasSelectionHandlersFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="unbounded")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
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
