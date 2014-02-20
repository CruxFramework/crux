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
package org.cruxframework.crux.widgets.rebind.tabcontainer;

import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.widgets.client.tabcontainer.Tab;
import org.cruxframework.crux.widgets.client.tabcontainer.TabContainer;
import org.cruxframework.crux.widgets.rebind.event.BeforeBlurEvtBind;
import org.cruxframework.crux.widgets.rebind.event.BeforeFocusEvtBind;

/**
 * 
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="tabViewContainer", library="widgets", targetWidget=TabContainer.class)
@TagChildren({
	@TagChild(TabContainerFactory.TabsProcessor.class)
})
public class TabContainerFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(tagName="view", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id"),
		@TagAttributeDeclaration(value="name", required=true),
		@TagAttributeDeclaration(value="closeable", type=Boolean.class, defaultValue="true"),
		@TagAttributeDeclaration(value="lazy", type=Boolean.class, defaultValue="true")
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onBeforeFocus"),
		@TagEventDeclaration("onBeforeBlur")
	})
	public static class TabsProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		protected BeforeFocusEvtBind beforeFocusEvtBind;
		protected BeforeBlurEvtBind beforeBlurEvtBind;

		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String name = context.readChildProperty("name");
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				id = name;
			}
			boolean closeable = true;
			String strCloseable = context.readChildProperty("closeable");
			if(strCloseable != null && strCloseable.trim().length() > 0)
			{
				closeable = Boolean.parseBoolean(strCloseable);
			}
			boolean lazy = true;
			String strLazy = context.readChildProperty("lazy");
			if(strLazy != null && strLazy.trim().length() > 0)
			{
				lazy = Boolean.parseBoolean(strLazy);
			}
			String beforeFocusEvt = context.readChildProperty(beforeFocusEvtBind.getEventName());
			String beforeBlurEvt = context.readChildProperty(beforeBlurEvtBind.getEventName());
			
			String rootWidget = context.getWidget();
			out.println(TabContainer.class.getCanonicalName()+".createView("+EscapeUtils.quote(name)+", "+ EscapeUtils.quote(id)+", new "+CreateCallback.class.getCanonicalName()+"(){");
			out.println("public void onViewCreated(View view){");
			boolean hasEvents = !StringUtils.isEmpty(beforeFocusEvt) || !StringUtils.isEmpty(beforeBlurEvt);
			if (hasEvents)
			{
				out.print("if (");
			}
			out.print(rootWidget+".add(view, "+lazy+", "+closeable+", true)");
			if (hasEvents)
			{
				out.print("){");
			}
			else
			{
				out.println(";");
			}
			String tab = ViewFactoryCreator.createVariableName("tab");
			out.print(Tab.class.getCanonicalName()+" " +tab+" = "+rootWidget+".getTab("+EscapeUtils.quote(id)+");");
			
			if (beforeFocusEvtBind == null) beforeFocusEvtBind = new BeforeFocusEvtBind(getWidgetCreator());
			if (beforeBlurEvtBind == null) beforeBlurEvtBind = new BeforeBlurEvtBind(getWidgetCreator());

			if (!StringUtils.isEmpty(beforeFocusEvt))
			{
				beforeFocusEvtBind.processEvent(out, beforeFocusEvt, tab, null);
			}
			if (!StringUtils.isEmpty(beforeBlurEvt))
			{
				beforeBlurEvtBind.processEvent(out, beforeBlurEvt, tab, null);
			}
			if (hasEvents)
			{
				out.println("}");
			}
			out.println("}");
			out.print("});");
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
