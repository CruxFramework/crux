package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.Slider;

/**
 * Factory for Slider SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="slider", targetWidget=Slider.class)

@TagAttributes({
	@TagAttribute("hThumbStyle"),
	@TagAttribute("hTrackStyle"),
	@TagAttribute("maxValueLabel"),
	@TagAttribute("thumbSrc"),
	@TagAttribute("title"),
	@TagAttribute("trackSrc"),
	@TagAttribute("vThumbStyle"),
	@TagAttribute("vTrackStyle"),
	@TagAttribute("minValueLabel")
}) 
	
public class SliderFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
