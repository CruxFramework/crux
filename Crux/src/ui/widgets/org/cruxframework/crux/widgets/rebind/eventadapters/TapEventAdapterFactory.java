/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */ 
package org.cruxframework.crux.widgets.rebind.eventadapters;

import org.cruxframework.crux.core.client.screen.eventadapter.TapEventAdapter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.json.JSONObject;


/**
 *
 */
@DeclarativeFactory(id="tapEventAdapter", library="widgets", targetWidget=TapEventAdapter.class)
@TagChildren({
	@TagChild(value=TapEventAdapterFactory.AdaptedWidgetProcessor.class, autoProcess=false)
})
public class TapEventAdapterFactory extends WidgetCreator<WidgetCreatorContext> 
{
	@TagConstraints(minOccurs="1", maxOccurs="1", widgetProperty="child")
	public static class AdaptedWidgetProcessor  extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
	{
		JSONObject child = ensureFirstChild(context.getWidgetElement(), false, context.getWidgetId());
		ensureWidget(child, context.getWidgetId());
		String childWidget = createChildWidget(out, child, context);
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+childWidget+");");
	}
}