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

import org.cruxframework.crux.core.client.screen.views.LazyPanel;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasOpenHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyCondition;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyConditions;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;

/**
 * Factory for DisclosurePanel widgets
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="disclosurePanel", library="gwt", targetWidget=DisclosurePanel.class)
@TagAttributes({
	@TagAttribute(value="open", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration("headerText")
})
@TagChildren({
	@TagChild(DisclosurePanelFactory.HeaderProcessor.class),
	@TagChild(DisclosurePanelFactory.ContentProcessor.class)
})	

public class DisclosurePanelFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasAnimationFactory<WidgetCreatorContext>, 
                  HasOpenHandlersFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String headerText = context.readWidgetProperty("headerText");
		String className = DisclosurePanel.class.getCanonicalName();
		if (!StringUtils.isEmpty(headerText))
		{
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(headerText)+");");
		}
		else
		{
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"();");
		}
		
		String open = context.readWidgetProperty("open");
		if (open == null || !StringUtils.unsafeEquals(open, "true"))
		{
			out.println(context.getWidget()+".addOpenHandler(new "+OpenHandler.class.getCanonicalName()+"<"+className+">() {");
			out.println("private boolean loaded = false;");
			out.println("public void onOpen("+OpenEvent.class.getCanonicalName()+"<"+className+"> event){"); 
			out.println("if (!loaded){");
			out.println(LazyPanel.class.getCanonicalName() +" widget = ("+LazyPanel.class.getCanonicalName()+")"+context.getWidget()+".getContent();");
			out.println("widget.ensureWidget();");
			out.println("loaded = true;");
			out.println("}");
			out.println("}");
			out.println("});");
		}
	}
	
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}

	@Override
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}

	@TagConstraints(minOccurs="0", tagName="widgetHeader")
	@TagChildren({
		@TagChild(WidgetHeaderProcessor.class)
	})	
	public static class HeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
		
	@TagConstraints(minOccurs="0", tagName="widgetContent")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})	
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="content")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="open", notEquals="true")
	})
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(widgetProperty="header")
	public static class WidgetHeaderProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
    @Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
}
