package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.MultiGroupDialog;

/**
 * Factory for MultiGroupDialog SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="multiGroupDialog", targetWidget=MultiGroupDialog.class)

@TagAttributes({
	@TagAttribute("title"),
	@TagAttribute("addLevelButtonTitle"),
	@TagAttribute("applyButtonTitle"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("copyLevelButtonTitle"),
	@TagAttribute("deleteLevelButtonTitle"),
	@TagAttribute("groupingFieldTitle"),
	@TagAttribute("invalidListPrompt"),
	@TagAttribute("levelDownButtonTitle"),
	@TagAttribute("propertyFieldTitle")
}) 
	
public class MultiGroupDialogFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
