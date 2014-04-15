package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.DateChooser;

/**
 * Factory for DateChooser SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="dateChooser", targetWidget=DateChooser.class)

@TagAttributes({
	@TagAttribute("alternateStyleSuffix"),
	@TagAttribute("applyButtonTitle"),
	@TagAttribute("baseBottomButtonStyle"),
	@TagAttribute("baseButtonStyle"),
	@TagAttribute("baseFiscalYearStyle"),
	@TagAttribute("baseNavButtonStyle"),
	@TagAttribute("baseWeekdayStyle"),
	@TagAttribute("baseWeekendStyle"),
	@TagAttribute("baseWeekStyle"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("disabledWeekdayStyle"),
	@TagAttribute("disabledWeekendStyle"),
	@TagAttribute("fiscalYearFieldTitle"),
	@TagAttribute("fiscalYearHeaderStyle"),
	@TagAttribute("nextYearIcon"),
	@TagAttribute("monthMenuStyle"),
	@TagAttribute("nextMonthIcon"),
	@TagAttribute("nextMonthIconRTL"),
	@TagAttribute("nextYearIcon"),
	@TagAttribute("nextYearIconRTL"),
	@TagAttribute("prevMonthIcon"),
	@TagAttribute("prevMonthIconRTL"),
	@TagAttribute("prevYearIcon"),
	@TagAttribute("nextYearIconRTL"),
	@TagAttribute("prevMonthIcon"),
	@TagAttribute("prevMonthIconRTL"),
	@TagAttribute("prevYearIcon"),
	@TagAttribute("prevYearIconRTL")
}) 

	
public class DateChooserFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
