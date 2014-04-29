package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.DateRangeDialog;

/**
 * Factory for DateRangeDialog SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="dateRangeDialog", targetWidget=DateRangeDialog.class)

@TagAttributes({
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("clearButtonTitle"),
	@TagAttribute("headerTitle"),
	@TagAttribute("okButtonTitle")
}) 

	
public class DateRangeDialogFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
