package org.cruxframework.crux.smartgwt.rebind.testesfy;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;

import com.smartgwt.client.widgets.layout.VStack;



@DeclarativeFactory(library="smartgwt", id="testes", targetWidget=VStack.class)

	
public class TestesFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
