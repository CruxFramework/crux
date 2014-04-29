package org.cruxframework.crux.smartgwt.rebind.toolbar;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.toolbar.ToolStripGroup;

/**
 * Factory for ToolStripGroup SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="toolStripGroup", targetWidget=ToolStripGroup.class)

@TagAttributes({
	@TagAttribute("bodyConstructor"),
	@TagAttribute("labelConstructor"),
	@TagAttribute("styleName"),
	@TagAttribute("titleStyle"),
	@TagAttribute("title")
}) 

public class ToolStripGroupFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
