package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.NavigationButton;

/**
 * Factory for NavigationButton SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="navigationButton", targetWidget=NavigationButton.class)

@TagAttributes({
	@TagAttribute("backBaseStyle"),
	@TagAttribute("baseStyle"),
	@TagAttribute("forwardBaseStyle")
}) 

public class NavigationButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
