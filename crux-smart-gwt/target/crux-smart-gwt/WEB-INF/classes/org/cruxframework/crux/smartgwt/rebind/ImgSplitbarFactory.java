package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.ImgSplitbar;

/**
 * Factory for ImgSplitbar SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="imgSplitbar", targetWidget=ImgSplitbar.class)

@TagAttributes({
	@TagAttribute("hSrc"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("src"),
	@TagAttribute("vSrc")
}) 

	
public class ImgSplitbarFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
