package org.cruxframework.crux.smartgwt.rebind.toolbar;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.toolbar.ToolStripResizer;

/**
 * Factory for RibbonBar SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="toolStripResizer", targetWidget=ToolStripResizer.class)

@TagAttributes({
	@TagAttribute("hSrc"),
	@TagAttribute("skinImgDir"),
	@TagAttribute("vSrc")
}) 

public class ToolStripResizerFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
