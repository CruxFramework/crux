package org.cruxframework.crux.smartgwt.rebind.tableview;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.tableview.TableView;

/**
 * Factory for TableView SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="tableView", targetWidget=TableView.class)

@TagAttributes({
	@TagAttribute("dataField"),
	@TagAttribute("descriptionField"),
	@TagAttribute("iconField"),
	@TagAttribute("infoField"),
	@TagAttribute("navIcon"),
	@TagAttribute("recordDataStyle"),
	@TagAttribute("recordDescriptionStyle"),
	@TagAttribute("recordInfoStyle"),
	@TagAttribute("recordNavigationProperty"),
	@TagAttribute("recordTitleStyle"),
	@TagAttribute("titleField"),
	@TagAttribute("wholeRecordNavIcon")
}) 

public class TableViewFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
