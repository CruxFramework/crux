package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.FieldPickerWindow;

/**
 * Factory for FieldPickerWindow SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="fieldPickerWindow", targetWidget=FieldPickerWindow.class)

@TagAttributes({
	@TagAttribute("title")
}) 
	
public class FieldPickerWindowFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
