package org.cruxframework.crux.smartgwt.rebind.tile;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.tile.TileGrid;



/**
 * Factory for TileGrid SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="tileGrid", targetWidget=TileGrid.class)

@TagAttributes({
	@TagAttribute("styleName"),
	@TagAttribute("tileConstructor"),
	@TagAttribute("tileValueAlign"),
	@TagAttribute("tileValueStyle"),
	@TagAttribute("fieldState"),
	@TagAttribute("fetchOperation"),
	@TagAttribute("updateOperation"),
	@TagAttribute("addOperation"),
	@TagAttribute("removeOperation"),
	@TagAttribute("duplicateDragMessage"),	
	@TagAttribute("hiliteProperty"),
	@TagAttribute("hiliteState"),
	@TagAttribute("dragTrackerStyle"),
	@TagAttribute("addFormulaFieldText"),
	@TagAttribute("editFormulaFieldText"),	
	@TagAttribute("addSummaryFieldText"),
	@TagAttribute("editSummaryFieldText"),
	@TagAttribute("titleField")
}) 

public class TileGridFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
