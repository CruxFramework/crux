package org.cruxframework.crux.smartgwt.rebind.form;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.form.FilterClause;

/**
 * Factory for FilterClause SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="filterClause", targetWidget=FilterClause.class)

@TagAttributes({
	@TagAttribute("fieldPickerTitle"),
	@TagAttribute("operatorPickerTitle"),
	@TagAttribute("removeButtonPrompt"),
	@TagAttribute("valueItemTitle")
}) 

public class FilterClauseFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
