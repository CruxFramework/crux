package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.ColumnTree;

/**
 * Factory for ColumnTree SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="columnTree", targetWidget=ColumnTree.class)

@TagAttributes({
	@TagAttribute("backButtonTitle"),
	@TagAttribute("closedIconSuffix"),
	@TagAttribute("customIconOpenProperty"),
	@TagAttribute("customIconProperty"),
	@TagAttribute("firstColumnTitle"),
	@TagAttribute("saveButtonTitle"),
	@TagAttribute("targetFieldsItemTitle"),
	@TagAttribute("title"),
	@TagAttribute("folderIcon"),
	@TagAttribute("nodeIcon"),
	@TagAttribute("openIconSuffix"),
	@TagAttribute("dropIconSuffix"),
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

public class ColumnTreeFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
