package org.cruxframework.crux.smartgwt.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.grid.RecordEditor;

/**
 * Factory for RecordEditor SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="recordEditor", targetWidget=RecordEditor.class)

@TagAttributes({
	@TagAttribute("actionButtonStyle"),
	@TagAttribute("baseStyle"),
	@TagAttribute("filterImg"),
	@TagAttribute("skinImgDir")
}) 

public class RecordEditorFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
