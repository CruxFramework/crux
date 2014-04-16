package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.ListGrid;

/**
 * Factory for ListGrid SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="listGrid", targetWidget=ListGrid.class)

@TagAttributes({
	@TagAttribute("bodyBackgroundColor"),
	@TagAttribute("bodyStyleName"),
	@TagAttribute("booleanBaseStyle"),
	@TagAttribute("booleanFalseImage"),
	@TagAttribute("booleanTrueImage"),
	@TagAttribute("cancelEditingConfirmationMessage"),
	@TagAttribute("canEditFieldAttribute"),
	@TagAttribute("dateInputFormat"),
	@TagAttribute("detailField"),
	@TagAttribute("discardEditsSaveButtonTitle"),	
	@TagAttribute("editFailedBaseStyle"),
	@TagAttribute("editFailedCSSText"),
	@TagAttribute("editPendingBaseStyle"),
	@TagAttribute("editPendingCSSText"),
	@TagAttribute("emptyCellValue"),	
	@TagAttribute("emptyMessage"),
	@TagAttribute("emptyMessageStyle"),
	@TagAttribute("expansionEditorSaveDialogPrompt"),
	@TagAttribute("gridSummaryRecordProperty"),
	@TagAttribute("groupByField"),	
	@TagAttribute("groupByText"),
	@TagAttribute("groupIcon"),
	@TagAttribute("groupNodeBaseStyle"),
	@TagAttribute("groupNodeStyle"),
	@TagAttribute("groupState"),	
	@TagAttribute("groupSummaryRecordProperty"),
	@TagAttribute("groupSummaryStyle"),
	@TagAttribute("groupTitleField"),
	@TagAttribute("headerBackgroundColor"),
	@TagAttribute("headerBaseStyle")
}) 

public class ListGridFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
