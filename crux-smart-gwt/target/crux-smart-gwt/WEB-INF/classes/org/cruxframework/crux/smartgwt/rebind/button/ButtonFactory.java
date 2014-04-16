package org.cruxframework.crux.smartgwt.rebind.button;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.button.Button;

/**
 * Factory for Button SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="button", targetWidget=Button.class)

@TagAttributes({
	@TagAttribute("title"),
	@TagAttribute("icon"),
	@TagAttribute("iconOrientation"),
	@TagAttribute("baseStyle"),
	@TagAttribute("iconAlign"),
	@TagAttribute("iconStyle"),
	@TagAttribute("radioGroup")
}) 
	
public class ButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
