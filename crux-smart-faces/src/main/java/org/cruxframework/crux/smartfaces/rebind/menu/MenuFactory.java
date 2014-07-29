/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.menu;

import java.util.LinkedList;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.Menu.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Menu.SmallType;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Context for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
class MenuContext extends WidgetCreatorContext
{
	LinkedList<String> itemStack = new LinkedList<String>();
	String currentItem;
}

/**
 * Factory for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
@DeclarativeFactory(id="menu", library=Constants.LIBRARY_NAME, targetWidget=Menu.class, 
	description="A menu class based in nav, ul and li html tags.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="largeType", type=LargeType.class, defaultValue="VerticalAccordion"),
	@TagAttributeDeclaration(value="smallType", type=SmallType.class, defaultValue="VerticalAccordion")
})
@TagChildren({
	@TagChild(MenuFactory.MenuItemProcessor.class)
})
public class MenuFactory extends WidgetCreator<MenuContext>
{
	@Override
	public void processAttributes(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		
		LargeType largeType = LargeType.VERTICAL_ACCORDION;
		String largeTypeProp = context.readWidgetProperty("largeType");
		if (largeTypeProp != null && largeTypeProp.length() > 0)
		{
			largeType = LargeType.getByName(largeTypeProp);
		}

		SmallType smallType = SmallType.VERTICAL_ACCORDION;
		String smallTypeProp = context.readWidgetProperty("smallType");
		if (smallTypeProp != null && smallTypeProp.length() > 0)
		{
			smallType = SmallType.getByName(smallTypeProp);
		}
		
		out.println("final "+className + " " + context.getWidget()+" = new "+ className +
				"("+LargeType.class.getCanonicalName()+"."+largeType.name()+","+SmallType.class.getCanonicalName()+"."+smallType.name()+");");
	}
	
	@Override
	public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		context.itemStack.add(context.getWidget());
	}
	
	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="open", type=Boolean.class),
		@TagAttributeDeclaration(value="style"),
		@TagAttributeDeclaration(value="styleName", supportsResources=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onSelect")
	})
	@TagChildren({
		@TagChild(ChoiceItemProcessor.class),
		@TagChild(MenuItemProcessor.class)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<MenuContext> implements HasPostProcessor<MenuContext>
	{
		StyleProcessor styleProcessor;
		boolean rootProcessed;
		
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			String item = getWidgetCreator().createVariableName("item");

			context.currentItem = item;
			
			String onSelectEvent = context.readChildProperty("onSelect");
			if (onSelectEvent != null && onSelectEvent.length() > 0)
			{
				out.println(item+".addSelectHandler(new "+SelectHandler.class.getCanonicalName()+"(){");
				out.println("public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");

				EvtProcessor.printEvtCall(out, onSelectEvent, "onSelect", SelectEvent.class, "event", getWidgetCreator());

				out.println("}");
				out.println("});");
			}
			
			setItemAttributes(out, context, item);
		}
		
		@Override
		public void postProcessChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
		{
			context.itemStack.removeFirst();			
		}
		
		/**
		 * Sets the item attributes before adding it to the parent.
		 * @param out
		 * @param context
		 * @param item
		 */
		private void setItemAttributes(SourcePrinter out, MenuContext context, String item)
		{
			if(context.readBooleanChildProperty("open", false))
			{
				out.println(item + ".open();");					
			}
			
			String style = context.readChildProperty("style");
			if (!StringUtils.isEmpty(style))
			{
				if (styleProcessor == null)
				{
			        styleProcessor = new StyleProcessor(getWidgetCreator());
				}
				styleProcessor.processAttribute(out, context, style);
			}
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				styleName = getWidgetCreator().getResourceAccessExpression(styleName);
				out.println(item + ".addClassName(" + EscapeUtils.quote(styleName) + ");");
			}
		}				
	}
	
	@TagChildren({
		@TagChild(ItemLabelProcessor.class),
		@TagChild(ItemHTMLProcessor.class),
		@TagChild(ItemWidgetProcessor.class)
	})
	@TagConstraints
	public static class ChoiceItemProcessor extends ChoiceChildProcessor<MenuContext>
	{
	}
	
	@TagConstraints(tagName="itemLabel", type=String.class)
	public static class ItemLabelProcessor extends WidgetChildProcessor<MenuContext>
	{
		@Override
		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
		{
			JSONObject jsonLabel = context.getChildElement();
			String label = "";
			try {
				label = EscapeUtils.quote(jsonLabel.getString("_text"));
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			String itemClassName = MenuItem.class.getCanonicalName();
			
			if(context.itemStack.size() == 1)
			{
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem("+ label +");");
			} 
			else
			{
				String parentItem = context.itemStack.getFirst();
				out.println(itemClassName + " " + context.currentItem+" = "+context.getWidget()+".addItem(" +parentItem +","+ label +");");
			}
			
			context.itemStack.addFirst(context.currentItem);
		}
	}
	
	@TagConstraints(tagName="itemHtml", type=HTMLTag.class)
	public static class ItemHTMLProcessor extends WidgetChildProcessor<MenuContext>
	{
//		@Override
//		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException 
//		{
//			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
//			context.isHTMLTitle = true;
//		}
	}
	
	@TagConstraints(tagName="itemWidget")
	public static class ItemWidgetProcessor extends WidgetChildProcessor<MenuContext> 
	{
		
		
		
	}
	
//	@TagConstraints(type=AnyWidget.class)
//	public static class WidgetContentProcessor extends WidgetChildProcessor<MenuContext> 
//	{
//		@Override
//		public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
//		{
//			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
//			String rootWidget = context.getWidget();
//			
//			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
//			if (childPartialSupport)
//			{
//				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
//			}
////			if (context.titleWidget != null)
////			{
////				if (context.titleWidgetPartialSupport)
////				{
////					out.println("if ("+context.titleWidgetClassType+".isSupported()){");
////				}
////				if(context.isWidgetTitle)
////				{
////					out.println(rootWidget+".add("+widget+", "+context.titleWidget+");");
////				} else
////				{
////					out.println(rootWidget+".add("+widget+", "+EscapeUtils.quote(context.titleWidget)+");");
////				}
////				if (context.titleWidgetPartialSupport)
////				{
////					out.println("}");
////				}
////			}
////			else
////			{
////				out.println(rootWidget+".add("+widget+", "+context.title+", "+context.isHTMLTitle+");");
////			}
//			if (childPartialSupport)
//			{
//				out.println("}");
//			}
//		}
//	}
	
	
	@Override
    public MenuContext instantiateContext()
    {
	    return new MenuContext();
    }
}