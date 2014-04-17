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
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBeforeSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.ClickEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyDownEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyPressEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyUpEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.json.JSONObject;

import com.google.gwt.user.client.ui.TabBar.Tab;

class TabPanelContext extends WidgetCreatorContext
{

	public JSONObject tabElement;
	public String title;
	public boolean isHTMLTitle;
	public String titleWidget;
	public boolean titleWidgetPartialSupport;
	public String titleWidgetClassType;
	
	public void clearAttributes()
    {
	    isHTMLTitle = false;
	    title = null;
	    titleWidget = null;
	    tabElement = null;
    }
	
}

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@TagAttributes({
	@TagAttribute(value="visibleTab", type=Integer.class, processor=AbstractTabPanelFactory.VisibleTabAttributeParser.class)
})
public abstract class AbstractTabPanelFactory extends CompositeFactory<TabPanelContext> 
       implements HasAnimationFactory<TabPanelContext>, 
                  HasBeforeSelectionHandlersFactory<TabPanelContext>, HasSelectionHandlersFactory<TabPanelContext>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabPanelContext>
	{
		public VisibleTabAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, TabPanelContext context, final String propertyValue)
        {
			String widget = context.getWidget();
			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
			printlnPostProcessing(widget+".selectTab("+Integer.parseInt(propertyValue)+");");
        }
	}	

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="tabEnabled", type=Boolean.class, defaultValue="true"),
		@TagAttributeDeclaration(value="tabWordWrap", type=Boolean.class, defaultValue="true")
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onClick"),
		@TagEventDeclaration("onKeyUp"),
		@TagEventDeclaration("onKeyDown"),
		@TagEventDeclaration("onKeyPress")
	})
	public static abstract class AbstractTabProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			context.tabElement = context.getChildElement();
		}
	}

	@TagConstraints(tagName="tabText", type=String.class)
	public static abstract class AbstractTextTabProcessor extends WidgetChildProcessor<TabPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			context.isHTMLTitle = false;
		}
	}
	
	@TagConstraints(tagName="tabHtml", type=HTMLTag.class)
	public static abstract class AbstractHTMLTabProcessor extends WidgetChildProcessor<TabPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			context.isHTMLTitle = true;
		}
	}
	
	@TagConstraints(type=AnyWidget.class)
	public static abstract class AbstractWidgetTitleProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			context.titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			context.titleWidgetPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			context.titleWidgetClassType = getWidgetCreator().getChildWidgetClassName(context.getChildElement());
		}
	}
	
	@TagConstraints(type=AnyWidget.class)
	public static abstract class AbstractWidgetContentProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String tabWidget = context.getWidget();
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			if (context.titleWidget != null)
			{
				if (context.titleWidgetPartialSupport)
				{
					out.println("if ("+context.titleWidgetClassType+".isSupported()){");
				}
				out.println(tabWidget+".add("+widget+", "+context.titleWidget+");");
				if (context.titleWidgetPartialSupport)
				{
					out.println("}");
				}
			}
			else
			{
				out.println(tabWidget+".add("+widget+", "+context.title+", "+context.isHTMLTitle+");");
			}
			updateTabState(out, context);
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
		
		private void updateTabState(SourcePrinter out, TabPanelContext context)
		{
			String enabled = context.tabElement.optString("enabled");
			String widget = context.getWidget();

			if (enabled != null && enabled.length() >0)
			{
				out.println(widget+".getTabBar().setTabEnabled("+widget+".getTabBar().getTabCount()-1, "+Boolean.parseBoolean(enabled)+");");
			}
			
			String currentTab = getWidgetCreator().createVariableName("currentTab");
			out.println(Tab.class.getCanonicalName()+" "+currentTab+" = "+widget+".getTabBar().getTab("+widget+".getTabBar().getTabCount()-1);");
			
			String wordWrap = context.tabElement.optString("wordWrap");
			if (wordWrap != null && wordWrap.trim().length() > 0)
			{
				out.println(currentTab+".setWordWrap("+Boolean.parseBoolean(wordWrap)+");");
			}

			if (clickEvtBind == null) clickEvtBind = new ClickEvtBind(getWidgetCreator());
			if (keyUpEvtBind == null) keyUpEvtBind = new KeyUpEvtBind(getWidgetCreator());
			if (keyPressEvtBind == null) keyPressEvtBind = new KeyPressEvtBind(getWidgetCreator());
			if (keyDownEvtBind == null) keyDownEvtBind = new KeyDownEvtBind(getWidgetCreator());

			String clickEvt = context.tabElement.optString(clickEvtBind.getEventName());
			if (!StringUtils.isEmpty(clickEvt))
			{
				clickEvtBind.processEvent(out, clickEvt, currentTab, null);
			}
			String keyUpEvt = context.tabElement.optString(keyUpEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyUpEvt))
			{
				keyUpEvtBind.processEvent(out, keyUpEvt, currentTab, null);
			}
			String keyPressEvt = context.tabElement.optString(keyPressEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyPressEvt))
			{
				keyPressEvtBind.processEvent(out, keyPressEvt, currentTab, null);
			}
			String keyDownEvt = context.tabElement.optString(keyDownEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyDownEvt))
			{
				keyDownEvtBind.processEvent(out, keyDownEvt, currentTab, null);
			}

			context.clearAttributes();
		}
		
		private static ClickEvtBind clickEvtBind;
		private static KeyUpEvtBind keyUpEvtBind;
		private static KeyPressEvtBind keyPressEvtBind;
		private static KeyDownEvtBind keyDownEvtBind;
	}
	
	@Override
	public TabPanelContext instantiateContext()
	{
	    return new TabPanelContext();
	}
}
