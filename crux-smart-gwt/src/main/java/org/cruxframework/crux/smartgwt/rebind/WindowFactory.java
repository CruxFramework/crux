package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.Window;

/**
 * Factory for Window SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="window", targetWidget=Window.class)

@TagAttributes({
	@TagAttribute("bodyColor"),
	@TagAttribute("bodyStyle"),
	@TagAttribute("contentLayout"),
	@TagAttribute("contentsType"),		
	@TagAttribute("headerSrc"),
	@TagAttribute("headerStyle"),
	@TagAttribute("hiliteBodyColor"),
	@TagAttribute("hiliteHeaderSrc"),		
	@TagAttribute("hiliteHeaderStyle"),
	@TagAttribute("modalMaskStyle"),
	@TagAttribute("printBodyStyle"),
	@TagAttribute("printHeaderStyle"),		
	@TagAttribute("src"),
	@TagAttribute("status"),
	@TagAttribute("title"),
	@TagAttribute("headerIcon")
}) 

public class WindowFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
