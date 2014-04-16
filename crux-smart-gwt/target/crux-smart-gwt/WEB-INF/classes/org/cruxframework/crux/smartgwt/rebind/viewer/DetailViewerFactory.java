package org.cruxframework.crux.smartgwt.rebind.viewer;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.viewer.DetailViewer;

/**
 * Factory for DetailViewer SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="detailViewer", targetWidget=DetailViewer.class)

@TagAttributes({
	@TagAttribute("blockSeparator"),
	@TagAttribute("blockStyle"),
	@TagAttribute("cellStyle"),
	@TagAttribute("configureFieldsText"),
	@TagAttribute("emptyCellValue"),	
	@TagAttribute("emptyMessage"),
	@TagAttribute("emptyMessageStyle"),
	@TagAttribute("fieldIdProperty"),
	@TagAttribute("headerStyle"),
	@TagAttribute("labelPrefix"),	
	@TagAttribute("labelStyle"),
	@TagAttribute("labelSuffix"),
	@TagAttribute("linkTextProperty"),
	@TagAttribute("loadingMessage"),
	@TagAttribute("loadingMessageStyle"),	
	@TagAttribute("printCellStyle"),
	@TagAttribute("printHeaderStyle"),
	@TagAttribute("printLabelStyle"),
	@TagAttribute("separatorStyle"),	
	@TagAttribute("fetchOperation"),
	@TagAttribute("updateOperation"),
	@TagAttribute("addOperation"),
	@TagAttribute("removeOperation"),
	@TagAttribute("ruplicateDragMessage"),	
	@TagAttribute("hiliteProperty"),
	@TagAttribute("hiliteState"),
	@TagAttribute("dragTrackerStyle"),
	@TagAttribute("addFormulaFieldText"),
	@TagAttribute("editFormulaFieldText"),	
	@TagAttribute("addSummaryFieldText"),
	@TagAttribute("editSummaryFieldText"),
	@TagAttribute("titleField")
}) 
	
public class DetailViewerFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
