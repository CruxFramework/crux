package org.cruxframework.crux.smartgwt.rebind.layout;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.layout.ImgSectionHeader;

/**
 * Factory for ImgSectionHeader SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="imgSectionHeader", targetWidget=ImgSectionHeader.class)

@TagAttributes({
	@TagAttribute("icon"),
	@TagAttribute("iconAlign"),
	@TagAttribute("iconOrientation"),
	@TagAttribute("prompt")
})

public class ImgSectionHeaderFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
