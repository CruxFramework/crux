package org.cruxframework.crux.smartgwt.rebind.form;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.form.ColorPicker;

/**
 * Factory for ColorPicker SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="colorPicker", targetWidget=ColorPicker.class)

@TagAttributes({
	@TagAttribute("basicColorLabel"),
	@TagAttribute("blueFieldPrompt"),
	@TagAttribute("blueFieldTitle"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("colorButtonBaseStyle"),
	@TagAttribute("crosshairImageURL"),
	@TagAttribute("defaultColor"),
	@TagAttribute("greenFieldPrompt"),	
	@TagAttribute("greenFieldTitle"),
	@TagAttribute("htmlFieldTitle"),
	@TagAttribute("hueFieldTitle"),
	@TagAttribute("lessButtonTitle"),	
	@TagAttribute("lumFieldPrompt"),
	@TagAttribute("lumFieldTitle"),
	@TagAttribute("moreButtonTitle"),
	@TagAttribute("okButtonTitle"),	
	@TagAttribute("opacitySliderLabel"),
	@TagAttribute("opacityText"),
	@TagAttribute("redFieldPrompt"),
	@TagAttribute("redFieldTitle"),	
	@TagAttribute("satFieldPrompt"),
	@TagAttribute("satFieldTitle"),
	@TagAttribute("selectedColorLabel"),
	@TagAttribute("swatchImageURL"),
	@TagAttribute("htmlColor")
}) 

public class ColorPickerFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
