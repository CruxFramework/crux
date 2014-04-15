package org.cruxframework.crux.smartgwt.rebind.layout;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.layout.NavigationBar;

/**
 * Factory for NavigationBar SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="navigationBar", targetWidget=NavigationBar.class)

@TagAttributes({
	@TagAttribute("leftButtonIcon"),
	@TagAttribute("leftButtonTitle"),
	@TagAttribute("rightButtonIcon"),
	@TagAttribute("rightButtonTitle"),
	@TagAttribute("title")
})

public class NavigationBarFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
