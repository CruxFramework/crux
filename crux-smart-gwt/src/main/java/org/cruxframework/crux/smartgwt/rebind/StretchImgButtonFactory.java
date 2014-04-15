package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.StretchImgButton;

/**
 * Factory for StretchImgButton SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="stretchImgButton", targetWidget=StretchImgButton.class)

@TagAttributes({
	@TagAttribute("baseStyle"),
	@TagAttribute("icon"),
	@TagAttribute("iconAlign"),
	@TagAttribute("iconOrientation"),
	@TagAttribute("labelSkinImgDir"),
	@TagAttribute("radioGroup"),
	@TagAttribute("src"),
	@TagAttribute("title"),
	@TagAttribute("titleStyle")
}) 
	
public class StretchImgButtonFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
