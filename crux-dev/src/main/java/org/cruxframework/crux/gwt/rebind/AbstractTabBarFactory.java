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

class TabBarContext extends WidgetCreatorContext
{
	public JSONObject tabElement;
}


/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@TagAttributes({
	@TagAttribute(value="visibleTab", type=Integer.class, processor=AbstractTabBarFactory.VisibleTabAttributeParser.class)
})
public abstract class AbstractTabBarFactory extends CompositeFactory<TabBarContext> 
       implements HasBeforeSelectionHandlersFactory<TabBarContext>, HasSelectionHandlersFactory<TabBarContext>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabBarContext>
	{
		public VisibleTabAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
		public void processAttribute(SourcePrinter out, TabBarContext context, String attributeValue)
		{
			String widget = context.getWidget();
			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
			printlnPostProcessing(widget+".selectTab("+Integer.parseInt(attributeValue)+");");
		}
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true"),
		@TagAttributeDeclaration(value="wordWrap", type=Boolean.class, defaultValue="true")
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onClick"),
		@TagEventDeclaration("onKeyUp"),
		@TagEventDeclaration("onKeyDown"),
		@TagEventDeclaration("onKeyPress")
	})
	public abstract static class AbstractTabProcessor extends WidgetChildProcessor<TabBarContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			context.tabElement =context.getChildElement();
		}
	}
	
	@TagConstraints(tagName="text", type=String.class)
	public abstract static class AbstractTextTabProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			out.println(context.getWidget()+".addTab("+title+");");
			updateTabState(out, context);
		}
	}
	
	@TagConstraints(tagName="html", type=HTMLTag.class)
	public abstract static class AbstractHTMLTabProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			out.println(context.getWidget()+".addTab("+title+", true);");
			updateTabState(out, context);
		}
	}
	
	@TagConstraints(type=AnyWidget.class)
	public abstract static class AbstractWidgetProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			String titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			out.println(context.getWidget()+".addTab("+titleWidget+");");
			updateTabState(out, context);
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}
	public abstract static class AbstractTabTitleProcessor extends WidgetChildProcessor<TabBarContext>
	{
		protected void updateTabState(SourcePrinter out, TabBarContext context)
		{
			String enabled = context.tabElement.optString("enabled");
			String widget = context.getWidget();

			if (enabled != null && enabled.length() >0)
			{
				out.println(widget+".setTabEnabled("+widget+".getTabCount()-1, "+Boolean.parseBoolean(enabled)+");");
			}

			String currentTab = getWidgetCreator().createVariableName("currentTab");
			out.println(Tab.class.getCanonicalName()+" "+currentTab+" = "+widget+".getTab("+widget+".getTabCount()-1);");

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

			context.tabElement = null;
		}
		private ClickEvtBind clickEvtBind;
		private KeyUpEvtBind keyUpEvtBind;
		private KeyPressEvtBind keyPressEvtBind;
		private KeyDownEvtBind keyDownEvtBind;
	}
	
	@Override
	public TabBarContext instantiateContext()
	{
	    return new TabBarContext();
	}
}
