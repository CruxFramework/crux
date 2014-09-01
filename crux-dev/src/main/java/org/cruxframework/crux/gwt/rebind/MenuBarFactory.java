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
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.client.ExecuteEvent;
import org.json.JSONObject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

class MenuBarContext extends WidgetCreatorContext
{
	String caption;
	boolean isHtml;
	
	public void clearAttributes() 
	{
		caption = null;
		isHtml = false;
	}
}

/**
 * A Factory for MenuBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="menuBar", library="gwt", targetWidget=MenuBar.class)
@TagAttributes({
	@TagAttribute(value="autoOpen", type=Boolean.class), 
	@TagAttribute(value="focusOnHoverEnabled", type=Boolean.class) 
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="vertical", type=Boolean.class)
})
@TagChildren({
	@TagChild(MenuBarFactory.MenutItemsProcessor.class)
})
public class MenuBarFactory extends WidgetCreator<MenuBarContext> 
       implements HasAnimationFactory<MenuBarContext>, HasCloseHandlersFactory<MenuBarContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, MenuBarContext context)
	{
		String className = MenuBar.class.getCanonicalName();
		out.println(className + " " + context.getWidget()+" = new "+className+"("+isMenuVertical(context.getWidgetElement())+");");
	}	

	/**
	 * @param element
	 * @return
	 */
	private boolean isMenuVertical(JSONObject element)
	{
		String verticalStr = element.optString("vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}
		return vertical;
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(MenutItemProcessor.class),
		@TagChild(MenutItemSeparatorProcessor.class)
	})
	public static class MenutItemsProcessor extends ChoiceChildProcessor<MenuBarContext> {}
	
	@TagConstraints(tagName="menuItem")
	@TagChildren({
		@TagChild(CaptionProcessor.class),
		@TagChild(MenuChildrenProcessor.class)
	})
	public static class MenutItemProcessor extends WidgetChildProcessor<MenuBarContext> {}
	
	@TagConstraints(tagName="separator")
	public static class MenutItemSeparatorProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			String widget = context.getWidget();
			out.println(widget+".addSeparator();");
		}
	}
	
	@TagConstraints(tagName="caption")
	@TagChildren({
		@TagChild(CaptionTypeProcessor.class)
	})
	public static class CaptionProcessor extends WidgetChildProcessor<MenuBarContext> {}
	
	@TagChildren({
		@TagChild(TextCaptionProcessor.class),
		@TagChild(HtmlCaptionProcessor.class)
	})
	public static class CaptionTypeProcessor extends ChoiceChildProcessor<MenuBarContext> {}

	@TagConstraints(tagName="textCaption")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true, supportsI18N=true)
	})
	public static class TextCaptionProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			context.caption = getWidgetCreator().getDeclaredMessage(context.readChildProperty("text"));
			context.isHtml = false;
		}
	}
	
	@TagConstraints(tagName="htmlCaption", type=HTMLTag.class)
	public static class HtmlCaptionProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
		{
			context.caption = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			context.isHtml = true;
		}
	}

	@TagConstraints(tagName="content", type=HTMLTag.class)
	@TagChildren({
		@TagChild(MenuChildrenTypeProcessor.class)
	})
	public static class MenuChildrenProcessor extends ChoiceChildProcessor<MenuBarContext> {}
	
	@TagChildren({
		@TagChild(CommandProcessor.class),
		@TagChild(SubMenuProcessor.class)
	})
	public static class MenuChildrenTypeProcessor extends ChoiceChildProcessor<MenuBarContext> {}
	
	@TagConstraints(tagName="command")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="onExecute", required=true)
	})
	public static class CommandProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out,  MenuBarContext context) throws CruxGeneratorException 
		{
			String executeEvt = context.readChildProperty("onExecute");
			if (executeEvt != null)
			{
				String item = createMenuItem(out, context);
				
				out.println(item+".setCommand(new "+Command.class.getCanonicalName()+"(){);");
				out.println("public void execute(){");

				EvtProcessor.printEvtCall(out, executeEvt, "onExecute", ExecuteEvent.class, 
										" new "+ExecuteEvent.class.getCanonicalName()+"<"+MenuBar.class.getCanonicalName()+
						                ">("+context.getWidget()+", "+context.getWidgetId()+")", getWidgetCreator());
				out.println("}");
				out.println("});");
			}
			context.clearAttributes();
		}
	}

	@TagConstraints(tagName="subMenu", type=MenuBarFactory.class)
	public static class SubMenuProcessor extends WidgetChildProcessor<MenuBarContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuBarContext context) throws CruxGeneratorException 
		{
			String subMenu = getSubMenu(getWidgetCreator(), out, context);
			String item = createMenuItem(out, context);
			out.println(item+".setSubMenu("+subMenu+");");
		}
	}
	
	/**
	 * Creates a subMenu
	 * @param widgetCreator 
	 * @param element
	 * @return
	 * @throws CruxGeneratorException 
	 */
	protected static String getSubMenu(WidgetCreator<?> widgetCreator, SourcePrinter out, MenuBarContext context) throws CruxGeneratorException
	{
		String widget = context.getWidget();
		String subMenu = widgetCreator.createChildWidget(out, context.getChildElement(), null, false, context);	
		out.println(subMenu+".setAutoOpen("+widget+".getAutoOpen());");
		out.println(subMenu+".setAnimationEnabled("+widget+".isAnimationEnabled());");
		return subMenu;
	}
	
	/**
	 * @param context
	 * @return
	 */
	protected static String createMenuItem(SourcePrinter out, MenuBarContext context)
	{
		String widget = context.getWidget();
		String menuItemClass = MenuItem.class.getCanonicalName();
		String menuItem = ViewFactoryCreator.createVariableName("menuItem");
		out.println(menuItemClass +" "+ menuItem+"="+
				widget+".addItem(new "+menuItemClass+"("+context.caption+", "+context.isHtml+", (Command)null));");
		return menuItem;
	}

	@Override
    public MenuBarContext instantiateContext()
    {
	    return new MenuBarContext();
    }
}
