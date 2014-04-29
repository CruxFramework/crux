package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.HiliteRule;

/**
 * Factory for HiliteRule SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="hiliteRule", targetWidget=HiliteRule.class)

@TagAttributes({
	@TagAttribute("backgroundColorTitle"),
	@TagAttribute("colorFieldTitle"),
	@TagAttribute("foregroundColorTitle"),
	@TagAttribute("iconFieldTitle"),
	@TagAttribute("removeButtonPrompt")
}) 

public class HiliteRuleFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
