/**
 * 
 */
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLParser extends AttributeProcessor<WidgetCreatorContext>
{
	public HTMLParser(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	@Override
    public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
    {
		String text = context.readWidgetProperty("text");
		if (text == null || text.length() ==0)
		{
			out.println(context.getWidget()+".setHTML("+EscapeUtils.quote(attributeValue)+");");
		}
    }
}

