package org.cruxframework.crux.smartgwt.rebind.toolbar;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.toolbar.ToolStrip;;



/**
 * Factory for ToolStrip SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="toolStrip", targetWidget=ToolStrip.class)

@TagAttributes({
	@TagAttribute("resizeBarClass"),
	@TagAttribute("separatorClass"),
	@TagAttribute("verticalStyleName")
}) 

public class ToolStripFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
