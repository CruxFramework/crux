package org.cruxframework.crux.smartgwt.rebind.toolbar;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.toolbar.ToolStripSeparator;



/**
 * Factory for ToolStripSeparator SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="toolStripSeparator", targetWidget=ToolStripSeparator.class)

@TagAttributes({
	@TagAttribute("hSrc"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("vSrc")
}) 

public class ToolStripSeparatorFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
