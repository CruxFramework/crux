package org.cruxframework.crux.smartgwt.rebind.plugins;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.plugins.Flashlet;

/**
 * Factory for Flashlet SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="flashlet", targetWidget=Flashlet.class)

@TagAttributes({
	@TagAttribute("classID"),
	@TagAttribute("codeBase"),
	@TagAttribute("name"),
	@TagAttribute("pluginsPage"),
	@TagAttribute("src")
}) 

public class FlashletFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
