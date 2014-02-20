package org.cruxframework.crux.widgets.rebind.disposal.viewchoicedisposal;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.widgets.client.disposal.panelchoicedisposal.PanelChoiceDisposal;

/**
 * 
 * @author Gesse Dafe
 *
 */
@DeclarativeFactory(library="widgets", id="viewChoiceDisposal", targetWidget=PanelChoiceDisposal.class)
public class ViewChoiceDisposalFactory extends WidgetCreator<WidgetCreatorContext>
{
    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }

    @Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final " + className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
