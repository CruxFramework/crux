package org.cruxframework.crux.smartgwt.rebind.form;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.form.DynamicForm;

/**
 * Factory for DynamicForm SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="dynamicForm", targetWidget=DynamicForm.class)

@TagAttributes({
	@TagAttribute("Action"),
	@TagAttribute("TitleField"),
	@TagAttribute("CancelParamName"),
	@TagAttribute("CancelParamValue"),
	@TagAttribute("CanEditFieldAttribute"),
	@TagAttribute("ErrorItemCellStyle"),
	@TagAttribute("ErrorsPreamble"),
	@TagAttribute("FormSubmitFailedWarning"),
	@TagAttribute("ItemHoverStyle"),
	@TagAttribute("ReadOnlyTextBoxStyle"),
	@TagAttribute("Target"),
	@TagAttribute("TitlePrefix"),
	@TagAttribute("TitleSuffix"),
	@TagAttribute("UnknownErrorMessage"),
	@TagAttribute("DuplicateDragMessage"),
	@TagAttribute("FetchOperation"),
	@TagAttribute("UpdateOperation"),
	@TagAttribute("AddOperation"),
	@TagAttribute("RemoveOperation"),
	@TagAttribute("DragTrackerStyle")
}) 
	
public class DynamicFormFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
