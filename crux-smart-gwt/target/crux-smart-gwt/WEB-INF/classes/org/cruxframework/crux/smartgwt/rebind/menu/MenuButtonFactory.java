package org.cruxframework.crux.smartgwt.rebind.menu;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.menu.MenuButton;

/**
 * Factory for MenuButton SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="menuButton", targetWidget=MenuButton.class)

@TagAttributes({
	@TagAttribute("menuAnimationEffect"),
	@TagAttribute("menuButtonImage"),
	@TagAttribute("menuButtonImageUp"),
	@TagAttribute("title")
}) 

public class MenuButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
