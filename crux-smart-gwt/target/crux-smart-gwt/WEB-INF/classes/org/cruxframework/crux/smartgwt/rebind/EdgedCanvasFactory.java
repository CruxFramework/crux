package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.EdgedCanvas;

/**
 * Factory for EdgedCanvas SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="edgedCanvas", targetWidget=EdgedCanvas.class)

@TagAttributes({
	@TagAttribute("centerBackgroundColor"),
	@TagAttribute("edgeColor"),
	@TagAttribute("edgeImage"),
	@TagAttribute("edgeStyleName"),
	@TagAttribute("skinImgDir")

})

public class EdgedCanvasFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
