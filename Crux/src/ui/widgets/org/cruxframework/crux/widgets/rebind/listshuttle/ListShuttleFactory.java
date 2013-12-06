package org.cruxframework.crux.widgets.rebind.listshuttle;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.widgets.client.listshuttle.ListShuttle;

@DeclarativeFactory(id = "listShuttle", library = "widgets", targetWidget = ListShuttle.class)
@TagChildren({ @TagChild(value = ListShuttleFactory.ContentProcessor.class) })
@TagAttributes({
	@TagAttribute(value = "availableHeader", required = true, supportsI18N = true),
	@TagAttribute(value = "selectedHeader", required = true, supportsI18N = true) })
public class ListShuttleFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}

	@TagConstraints(minOccurs = "0", maxOccurs = "1")
	public static class ContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext>
	{
	}
}
