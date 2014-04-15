package org.cruxframework.crux.smartgwt.rebind.cube;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.cube.CubeGrid;

/**
 * Factory for CubeGrid SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="cubeGrid", targetWidget=CubeGrid.class)

@TagAttributes({
	@TagAttribute("baseStyle"),
	@TagAttribute("bodyStyleName"),
	@TagAttribute("cellIdProperty"),
	@TagAttribute("chartConstructor"),	
	@TagAttribute("colHeaderBaseStyle"),
	@TagAttribute("facetLabelHoverStyle"),
	@TagAttribute("facetValueHoverStyle"),	
	@TagAttribute("innerHeaderBaseStyle"),
	@TagAttribute("metricFacetId"),
	@TagAttribute("rollupValue"),
	@TagAttribute("rowHeaderBaseStyle"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("styleName"),
	@TagAttribute("valueProperty"),
	@TagAttribute("valueTitle")
}) 
	
public class CubeGridFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
