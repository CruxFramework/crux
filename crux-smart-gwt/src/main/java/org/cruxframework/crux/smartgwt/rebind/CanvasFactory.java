package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.Canvas;

/**
 * Factory for Canvas SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="canvas", targetWidget=Canvas.class)

@TagAttributes({
	@TagAttribute("accessKey"),
	@TagAttribute("appImgDir"),
	@TagAttribute("ariaRole"),
	@TagAttribute("backgroundColor"),	
	@TagAttribute("backgroundImage"),
	@TagAttribute("backgroundPosition"),
	@TagAttribute("border"),
	@TagAttribute("contents"),	
	@TagAttribute("dataPath"),
	@TagAttribute("dragType"),
	@TagAttribute("dropTypes"),
	@TagAttribute("edgeBackgroundColor"),	
	@TagAttribute("edgeCenterBackgroundColor"),
	@TagAttribute("edgeImage"),
	@TagAttribute("groupBorderCSS"),
	@TagAttribute("groupLabelBackgroundColor"),	
	@TagAttribute("groupLabelStyleName"),
	@TagAttribute("groupTitle"),
	@TagAttribute("height"),
	@TagAttribute("hoverStyle"),	
	@TagAttribute("left"),
	@TagAttribute("menuConstructor"),
	@TagAttribute("prompt"),
	@TagAttribute("resizeBarTarget"),	
	@TagAttribute("scrollbarConstructor"),
	@TagAttribute("shadowImage"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("snapAxis"),	
	@TagAttribute("snapEdge"),
	@TagAttribute("snapHDirection"),
	@TagAttribute("snapTo"),	
	@TagAttribute("snapVDirection"),
	@TagAttribute("styleName"),
	@TagAttribute("top"),
	@TagAttribute("facetId"),
	@TagAttribute("prefix")
}) 
	
public class CanvasFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
