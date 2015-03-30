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
package org.cruxframework.crux.smartfaces.rebind.viewcontainer;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.smartfaces.client.viewcontainer.SimpleViewContainer;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * A factory for SimpleViewContainer Widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="simpleViewContainer", library=Constants.LIBRARY_NAME, targetWidget=SimpleViewContainer.class,
					description="A single view container that allows parameters on load and activate events")
@TagChildren({
	@TagChild(SimpleViewContainerFactory.ViewProcessor.class)
})
public class SimpleViewContainerFactory extends WidgetCreator<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="1", tagName="view",
    		description="A view to be rendered into this view container.")
    @TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", description="The view identifier."),
		@TagAttributeDeclaration(value="name", required=true, description="The name of the view.")
    })
    public static class ViewProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
    {
    	@Override
    	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
    	{
    		String viewId = context.readChildProperty("id");
    		String viewName = context.readChildProperty("name");
    		
    		if (StringUtils.isEmpty(viewId))
    		{
    			viewId = viewName;
    		}
    		out.println(context.getWidget()+".loadView("+EscapeUtils.quote(viewName)+","+EscapeUtils.quote(viewId)+", true);");
    	}
    }
    
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
