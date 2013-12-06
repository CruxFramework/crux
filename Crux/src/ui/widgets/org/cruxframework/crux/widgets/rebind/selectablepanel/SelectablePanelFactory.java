package org.cruxframework.crux.widgets.rebind.selectablepanel;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.widgets.client.selectablepanel.SelectablePanel;
import org.cruxframework.crux.widgets.rebind.event.SelectEvtBind;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@DeclarativeFactory(library="widgets", id="selectablePanel", targetWidget=SelectablePanel.class)

@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false")
})
@TagEvents({
	@TagEvent(SelectEvtBind.class)
})
@TagChildren({
	@TagChild(SelectablePanelFactory.WidgetContentProcessor.class)
})
public class SelectablePanelFactory extends PanelFactory<WidgetCreatorContext>
						implements 	HasAllFocusHandlersFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>,
									HasHTMLFactory<WidgetCreatorContext>, FocusableFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
    
    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
