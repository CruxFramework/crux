package org.cruxframework.crux.smartgwt.rebind.tree;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.tree.TreeGrid;

/**
 * Factory for TreeGrid SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="treeGrid", targetWidget=TreeGrid.class)

@TagAttributes({
	@TagAttribute("cantDragIntoChildMessage"),
	@TagAttribute("cantDragIntoSelfMessage"),
	@TagAttribute("closedIconSuffix"),
	@TagAttribute("connectorImage"),
	@TagAttribute("customIconDropProperty"),
	@TagAttribute("customIconOpenProperty"),
	@TagAttribute("customIconProperty"),
	@TagAttribute("dropIconSuffix"),
	@TagAttribute("folderIcon"),
	@TagAttribute("manyItemsImage"),
	@TagAttribute("nodeIcon"),
	@TagAttribute("offlineNodeMessage"),
	@TagAttribute("openerImage"),
	@TagAttribute("openIconSuffix"),
	@TagAttribute("parentAlreadyContainsChildMessage"),
	@TagAttribute("selectionProperty"),
	@TagAttribute("treeFieldTitle"),
	@TagAttribute("treeRootValue")
}) 
	
public class TreeGridFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
