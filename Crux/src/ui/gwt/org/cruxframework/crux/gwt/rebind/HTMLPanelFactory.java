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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.client.CruxHTMLPanel;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="HTMLPanel", library="gwt", targetWidget=HTMLPanel.class, htmlContainer=true)
@TagChildren({
	@TagChild(value=HTMLPanelFactory.ContentProcessor.class, autoProcess=false)
})
public class HTMLPanelFactory extends AbstractHTMLPanelFactory
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = CruxHTMLPanel.class.getCanonicalName();
		String id = context.readWidgetProperty("id");
        if(StringUtils.isEmpty(id))
        {
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+getScreen().getId()+"], there is an widget of type ["+context.readChildProperty("_type")+"] without id.");
        }
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(id)+", false);");
		createChildren(out, context);
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=AnyTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
}
