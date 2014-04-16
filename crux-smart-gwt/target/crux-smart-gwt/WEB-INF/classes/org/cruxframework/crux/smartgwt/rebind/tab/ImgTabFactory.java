package org.cruxframework.crux.smartgwt.rebind.tab;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.tab.ImgTab;

/**
 * Factory for ImgTab SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="imgTab", targetWidget=ImgTab.class)

@TagAttributes({
	@TagAttribute("baseStyle"),
	@TagAttribute("labelSkinImgDir"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("src"),
	@TagAttribute("titleStyle")
}) 

public class ImgTabFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
