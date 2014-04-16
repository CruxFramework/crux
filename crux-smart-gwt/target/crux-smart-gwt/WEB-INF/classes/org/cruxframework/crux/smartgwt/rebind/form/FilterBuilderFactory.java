package org.cruxframework.crux.smartgwt.rebind.form;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.form.FilterBuilder;

/**
 * Factory for FilterBuilder SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="filterBuilder", targetWidget=FilterBuilder.class)

@TagAttributes({
	@TagAttribute("addButtonPrompt"),
	@TagAttribute("fieldPickerTitle"),
	@TagAttribute("fieldPickerWidth"),
	@TagAttribute("inlineAndNotTitle"),
	@TagAttribute("inlineAndTitle"),
	@TagAttribute("inlineOrTitle"),
	@TagAttribute("matchAllTitle"),
	@TagAttribute("matchAnyTitle"),
	@TagAttribute("matchNoneTitle"),
	@TagAttribute("missingFieldPrompt"),	
	@TagAttribute("modeSwitcherAdvancedMessage"),
	@TagAttribute("modeSwitcherFlattenWarningMessage"),
	@TagAttribute("modeSwitcherSimpleMessage"),
	@TagAttribute("operatorPickerTitle"),
	@TagAttribute("operatorPickerWidth"),
	@TagAttribute("radioOperatorTitle"),
	@TagAttribute("rangeSeparator"),
	@TagAttribute("removeButtonPrompt"),
	@TagAttribute("subClauseButtonPrompt"),
	@TagAttribute("subClauseButtonTitle"),
	@TagAttribute("topOperatorItemWidth"),
	@TagAttribute("topOperatorTitle"),
	@TagAttribute("valueItemWidth")
}) 

public class FilterBuilderFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
