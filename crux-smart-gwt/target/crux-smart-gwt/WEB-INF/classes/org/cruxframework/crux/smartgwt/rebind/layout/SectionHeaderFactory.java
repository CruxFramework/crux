package org.cruxframework.crux.smartgwt.rebind.layout;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.layout.SectionHeader;

/**
 * Factory for SectionHeader SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="sectionHeader", targetWidget=SectionHeader.class)

@TagAttributes({
	@TagAttribute("baseStyle"),
	@TagAttribute("icon")
})

public class SectionHeaderFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
