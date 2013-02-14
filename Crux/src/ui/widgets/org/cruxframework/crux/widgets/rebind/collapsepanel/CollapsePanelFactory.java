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
package org.cruxframework.crux.widgets.rebind.collapsepanel;

import org.cruxframework.crux.core.client.screen.LazyPanel;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyCondition;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyConditions;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.CellPanelContext;
import org.cruxframework.crux.widgets.client.collapsepanel.CollapsePanel;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeExpandEvent;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeExpandHandler;
import org.cruxframework.crux.widgets.rebind.event.BeforeCollapseEvtBind;
import org.cruxframework.crux.widgets.rebind.event.BeforeExpandEvtBind;
import org.cruxframework.crux.widgets.rebind.titlepanel.AbstractTitlePanelFactory;


/**
 * Factory for Collapse Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="collapsePanel", library="widgets", targetWidget=CollapsePanel.class)
@TagEvents({
	@TagEvent(BeforeCollapseEvtBind.class),
	@TagEvent(BeforeExpandEvtBind.class)
})
@TagAttributes({
	@TagAttribute(value="collapsed", type=Boolean.class),
	@TagAttribute(value="collapsible", type=Boolean.class)
})
@TagChildren({
	@TagChild(CollapsePanelFactory.TitleProcessor.class),
	@TagChild(CollapsePanelFactory.BodyProcessor.class)
})
public class CollapsePanelFactory extends AbstractTitlePanelFactory
{
	@Override
	public void instantiateWidget(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"();");

		String collapsible = context.readWidgetProperty("collapsible");
		String collapsed = context.readWidgetProperty("collapsed");
		if ((collapsible == null || !StringUtils.unsafeEquals(collapsible, "false")) &&
			(collapsed != null && StringUtils.unsafeEquals(collapsed, "true")))
		{
			out.println(context.getWidget()+".addBeforeExpandHandler(new "+BeforeExpandHandler.class.getCanonicalName()+"(){");
			out.println("private boolean loaded = false;");
			out.println("public void onBeforeExpand("+BeforeExpandEvent.class.getCanonicalName()+" event){");
			out.println("if (!loaded){");
			String lazyWidgetVar = ViewFactoryCreator.createVariableName("widget");
			out.println(LazyPanel.class.getCanonicalName()+" "+lazyWidgetVar+" = ("+LazyPanel.class.getCanonicalName()+")"+context.getWidget()+".getContentWidget();");
			out.println(lazyWidgetVar+".ensureWidget();");
			out.println("loaded = true;");
			out.println("}");
			out.println("}");
			out.println("});");
		}		
	}	
	
	@TagConstraints(tagName="title", minOccurs="0")
	@TagChildren({
		@TagChild(TitleChildrenProcessor.class)
	})
	public static class TitleProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(CollapsePanelHTMLChildProcessor.class),
		@TagChild(CollapsePanelTextChildProcessor.class),
		@TagChild(CollapsePanelWidgetProcessor.class)
	})
	public static class TitleChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(TitleWidgetTitleProcessor.class)
	})
	public static class CollapsePanelWidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}
	
	@TagConstraints(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}
	
	@TagConstraints(tagName="body", minOccurs="0")
	@TagChildren({
		@TagChild(BodyChildrenProcessor.class)
	})
	public static class BodyProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(CollapsePanelBodyHTMLChildProcessor.class),
		@TagChild(CollapsePanelBodyTextChildProcessor.class),
		@TagChild(CollapsePanelBodyWidgetProcessor.class)
	})
	public static class BodyChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(BodyWidgetContentProcessor.class)
	})
	public static class CollapsePanelBodyWidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}
	
	@TagConstraints(widgetProperty="contentWidget")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="collapsible", notEquals="false"),
		@TagChildLazyCondition(property="collapsed", equals="true")
	})
	public static class BodyWidgetContentProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}

	public static class CollapsePanelHTMLChildProcessor extends HTMLChildProcessor{}
	public static class CollapsePanelTextChildProcessor extends TextChildProcessor{}
	public static class CollapsePanelBodyHTMLChildProcessor extends BodyHTMLChildProcessor{}
	public static class CollapsePanelBodyTextChildProcessor extends BodyTextChildProcessor{}
	
}