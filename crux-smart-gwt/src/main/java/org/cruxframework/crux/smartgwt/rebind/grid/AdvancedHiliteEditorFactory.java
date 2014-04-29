package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.AdvancedHiliteEditor;

/**
 * Factory for AdvancedHiliteEditor SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="advancedHiliteEditor", targetWidget=AdvancedHiliteEditor.class)

@TagAttributes({
	@TagAttribute("appearanceGroupTitle"),
	@TagAttribute("callback"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("filterGroupTitle"),
	@TagAttribute("invalidHilitePrompt"),
	@TagAttribute("saveButtonTitle"),
	@TagAttribute("targetFieldsItemTitle"),
	@TagAttribute("title")
})

public class AdvancedHiliteEditorFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
