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
package org.cruxframework.crux.widgets.rebind.dialogcontainer;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
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
import org.cruxframework.crux.widgets.client.dialogcontainer.DialogViewContainer;

/**
 * 
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="dialogViewContainer", library="widgets", targetWidget=DialogViewContainer.class, attachToDOM=false)
@TagChildren({
	@TagChild(DialogViewContainerFactory.ViewProcessor.class)
})
public class DialogViewContainerFactory extends WidgetCreator<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="1", tagName="view")
    @TagAttributesDeclaration({
    	@TagAttributeDeclaration(value="name", required=true)
    })
    public static class ViewProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
    {
    	@Override
    	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
    	{
    		out.println(context.getWidget()+".loadView("+EscapeUtils.quote(context.readChildProperty("name"))+", true);");
    	}
    }
    
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
