package org.cruxframework.crux.smartgwt.rebind.plugins;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.plugins.Applet;

/**
 * Factory for Applet SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="applet", targetWidget=Applet.class)

@TagAttributes({
	@TagAttribute("alt"),
	@TagAttribute("altHTML"),
	@TagAttribute("archive"),
	@TagAttribute("classID"),
	@TagAttribute("code"),
	@TagAttribute("codeBase"),
	@TagAttribute("name"),
	@TagAttribute("objectCodeBase"),
	@TagAttribute("pluginID"),
	@TagAttribute("src"),
	@TagAttribute("useTag")
}) 

public class AppletFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
