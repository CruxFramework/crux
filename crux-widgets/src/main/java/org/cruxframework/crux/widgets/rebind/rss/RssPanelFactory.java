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
package org.cruxframework.crux.widgets.rebind.rss;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.widgets.client.rss.RssPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="rssPanel", library="widgets", targetWidget=RssPanel.class, 
	description="a panel to display RSS content.")
@TagAttributes({
	@TagAttribute(value="title", required=true, supportsI18N=true),
	@TagAttribute(value="titleStyleName", supportsResources=true)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="maxTitleSize", type=Integer.class),
	@TagAttributeDeclaration(value="entryTitleStyleName", supportsResources=true),
	@TagAttributeDeclaration(value="entryDateStyleName", supportsResources=true)
})
public class RssPanelFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String maxTitleSize 	   = context.readWidgetProperty("maxTitleSize");
		String entryTitleStyleName = context.readWidgetProperty("entryTitleStyleName");
		String entryDateStyleName  = context.readWidgetProperty("entryDateStyleName");
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+
					(StringUtils.isEmpty(maxTitleSize) ? "null" : Integer.parseInt(maxTitleSize)) + "," +
					(StringUtils.isEmpty(entryTitleStyleName) ? "null" : "\"" + entryTitleStyleName + "\"") + "," + 
					(StringUtils.isEmpty(entryDateStyleName) ? "null" : "\"" + entryDateStyleName + "\"") +
					");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
