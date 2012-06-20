package org.cruxframework.crux.crossdevice.rebind.labeledtextbox;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.crossdevice.client.labeledtextbox.LabeledTextBox;
import org.cruxframework.crux.gwt.rebind.FocusWidgetFactory;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@DeclarativeFactory(library="crossDevice", id="labeledTextBox", targetWidget=LabeledTextBox.class)

@TagAttributes({
	@TagAttribute("value"),
	@TagAttribute(value="readOnly", type=Boolean.class),
	@TagAttribute(value="placeholder", type=String.class)
})
public class LabeledTextBoxFactory extends FocusWidgetFactory<WidgetCreatorContext>
						implements 	HasChangeHandlersFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>,
									HasTextFactory<WidgetCreatorContext>
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
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}

}
