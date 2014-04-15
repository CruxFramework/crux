package org.cruxframework.crux.smartgwt.rebind.menu;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.menu.IMenuButton;

/**
 * Factory for IMenuButton SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="iMenuButton", targetWidget=IMenuButton.class)

@TagAttributes({
	@TagAttribute("menuAnimationEffect"),
	@TagAttribute("menuButtonImage"),
	@TagAttribute("menuButtonImageUp"),
	@TagAttribute("title")
}) 

public class IMenuButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
