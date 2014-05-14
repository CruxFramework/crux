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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyCondition;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyConditions;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.LazyPanel;

/**
 * A Panel which content is only rendered when it becomes visible for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="lazyPanel", library="gwt", targetWidget=LazyPanel.class)
@TagChildren({
	@TagChild(LazyPanelFactory.WidgetContentProcessor.class)
})
public class LazyPanelFactory extends PanelFactory<WidgetCreatorContext> 
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println(className + " " + context.getWidget()+" = new "+className+"(){");
		out.println("protected Widget createWidget(){");
		out.println("return null;");
		out.println("}");
		out.println("public void setVisible(boolean visible){");
		out.println("setVisible(getElement(), visible);");
		out.println("}");
		out.println("};");
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="1")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="visible", notEquals="true")
	})	
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
