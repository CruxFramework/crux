package org.cruxframework.crux.widgets.rebind.button;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.button.DownloadButton;
import org.cruxframework.crux.widgets.rebind.event.SelectEvtBind;

/**
 * @author Samuel Almeida Cardoso - <code>samuel.cardoso@cruxframework.org</code>
 *
 */
@DeclarativeFactory(library="widgets", id="downloadButton", targetWidget=DownloadButton.class)
@TagChildren({
	@TagChild(value=DownloadButtonFactory.ContentProcessor.class, autoProcess=false)
})
@TagEvents({
	@TagEvent(SelectEvtBind.class)
})
public class DownloadButtonFactory extends WidgetCreator<WidgetCreatorContext>
						implements HasEnabledFactory<WidgetCreatorContext>, HasTextFactory<WidgetCreatorContext>, HasSelectionHandlersFactory<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
