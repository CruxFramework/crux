package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.HiliteEditor;

/**
 * Factory for HiliteEditor SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="hiliteEditor", targetWidget=HiliteEditor.class)

@TagAttributes({
	@TagAttribute("addAdvancedRuleButtonTitle"),
	@TagAttribute("availableFieldsColumnTitle"),
	@TagAttribute("callback"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("saveButtonTitle")
}) 

public class HiliteEditorFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
