/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.list;

import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasPagedDataProviderFactory.PagedDataProviderChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.smartfaces.client.list.WidgetList;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="widgetList", library=Constants.LIBRARY_NAME, targetWidget=WidgetList.class, 
					description="A list of widgets that use a DataProvider to provide data and a widgetFactory "
							+ "to bound the data to a widget. This list can be paged by a Pager.")
@TagChildren({
	@TagChild(PagedDataProviderChildren.class),
	@TagChild(value=WidgetListFactory.WidgetListChildCreator.class, autoProcess=false)
})
public class WidgetListFactory extends AbstractPageableFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		JSONObject widgetCreatorChild = null;
		JSONObject dataChild = null;
		JSONArray children = ensureChildren(context.getWidgetElement(), false, context.getWidgetId());
		for (int i=0; i< children.length(); i++)
		{
			JSONObject child = children.optJSONObject(i);
			if (getChildName(child).startsWith("widget"))
			{
				widgetCreatorChild = child;
			}
			else
			{
				dataChild = child;
			}
		}
		
		JClassType dataObject = getDataObject(context.getWidgetId(), dataChild);
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String className = getWidgetClassName()+"<"+dataObjectName+">";

		String widgetListFactory = createVariableName("widgetListFactory");
		String widgetFactoryClassName = WidgetFactory.class.getCanonicalName()+"<"+dataObjectName+">";
		
		out.print("final " + widgetFactoryClassName + " " + widgetListFactory + " = ");
		
		generateWidgetCreationForCell(out, context, widgetCreatorChild, dataObject);

		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+widgetListFactory+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}