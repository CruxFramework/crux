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
package org.cruxframework.crux.widgets.rebind.dynatabs;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabs;
import org.cruxframework.crux.widgets.client.dynatabs.Tab;
import org.cruxframework.crux.widgets.rebind.event.BeforeBlurEvtBind;
import org.cruxframework.crux.widgets.rebind.event.BeforeCloseEvtBind;
import org.cruxframework.crux.widgets.rebind.event.BeforeFocusEvtBind;


/**
 * Factory for Decorated Button widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="dynaTabs", library="widgets", targetWidget=DynaTabs.class)
@TagChildren({
	@TagChild(DynaTabsFactory.DynaTabProcessor.class)
})
public class DynaTabsFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(tagName="tab", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration(value="url", required=true),
		@TagAttributeDeclaration("label"),
		@TagAttributeDeclaration(value="closeable", type=Boolean.class)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onBeforeFocus"),
		@TagEventDeclaration("onBeforeBlur"),
		@TagEventDeclaration("onBeforeClose")
	})
	public static class DynaTabProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		protected BeforeFocusEvtBind beforeFocusEvtBind;
		protected BeforeBlurEvtBind beforeBlurEvtBind;
		protected BeforeCloseEvtBind beforeCloseEvtBind;

		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String id = context.readChildProperty("id");
			String label = context.readChildProperty("label");
			label = (label != null && label.length() > 0) ? getWidgetCreator().getDeclaredMessage(label) : EscapeUtils.quote("");
			String url = context.readChildProperty("url");
						
			boolean closeable = true;
			String strCloseable = context.readChildProperty("closeable");
			if(strCloseable != null && strCloseable.trim().length() > 0)
			{
				closeable = Boolean.parseBoolean(strCloseable);
			}
			
			String rootWidget = context.getWidget();
			String tab = getWidgetCreator().createVariableName("tab");
			out.println(Tab.class.getCanonicalName()+" "+tab+" = "+rootWidget+".openTab("+EscapeUtils.quote(id)+", "+
					label+", "+EscapeUtils.quote(url)+", "+closeable+", false);");
			
			if (beforeFocusEvtBind == null) beforeFocusEvtBind = new BeforeFocusEvtBind(getWidgetCreator());
			if (beforeBlurEvtBind == null) beforeBlurEvtBind = new BeforeBlurEvtBind(getWidgetCreator());
			if (beforeCloseEvtBind == null) beforeCloseEvtBind = new BeforeCloseEvtBind(getWidgetCreator());

			String beforeFocusEvt = context.readChildProperty(beforeFocusEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeFocusEvt))
			{
				beforeFocusEvtBind.processEvent(out, beforeFocusEvt, tab, null);
			}
			String beforeBlurEvt = context.readChildProperty(beforeBlurEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeBlurEvt))
			{
				beforeBlurEvtBind.processEvent(out, beforeBlurEvt, tab, null);
			}
			String beforeCloseEvt = context.readChildProperty(beforeCloseEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeCloseEvt))
			{
				beforeCloseEvtBind.processEvent(out, beforeCloseEvt, tab, null);
			}
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}