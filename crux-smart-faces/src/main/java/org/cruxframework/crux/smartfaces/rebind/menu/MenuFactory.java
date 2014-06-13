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
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.SelectionEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.Menu.Orientation;
import org.cruxframework.crux.smartfaces.client.menu.Menu.Type;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * Context for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
class MenuContext extends WidgetCreatorContext
{
	LinkedList<String> itemStack = new LinkedList<String>();
	Orientation orientation;
	Type type;
}

/**
 * Factory for Menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
@DeclarativeFactory(id="menu", library=Constants.LIBRARY_NAME, targetWidget=Menu.class, 
	description="A menu class based in nav, ul and li html tags.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration("orientation"),
	@TagAttributeDeclaration("type")
})
@TagChildren({
	@TagChild(MenuFactory.MenuItemProcessor.class)
})
@TagEvents({
	@TagEvent(SelectionEvtBind.class)
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
		
		String orientation = context.readWidgetProperty("orientation");
		if (orientation != null && orientation.length() > 0)
		{
			context.orientation = Orientation.valueOf(orientation);
		}
		
		String type = context.readWidgetProperty("type");
		if (type != null && type.length() > 0)
		{
			context.type = Type.valueOf(type);
		}
		
		out.println("final "+className + " " + context.getWidget()+" = new "+ className +
				"("+Orientation.class.getCanonicalName()+"."+context.orientation+","+Type.class.getCanonicalName()+"."+context.type+");");
	}
	
	@Override
	public void processChildren(SourcePrinter out, MenuContext context) throws CruxGeneratorException
	{
		context.itemStack.add(context.getWidget());
	}
	
	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", supportsI18N=true, required=true),
		@TagAttributeDeclaration(value="open", type=Boolean.class),
		@TagAttributeDeclaration(value="style"),
		@TagAttributeDeclaration(value="styleName")
	})
	@TagChildren({
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
			
			String label = context.getChildElement().optString("label");
			label = getWidgetCreator().getDeclaredMessage(label);
			
			String itemClassName = MenuItem.class.getCanonicalName();
			
			if(context.itemStack.size() == 1)
			{
				out.println(itemClassName + " " + item+" = "+context.getWidget()+".addItem("+ label +");");
			} else
			{
				String parentWidget = context.itemStack.getFirst();
				out.println(itemClassName + " " + item+" = "+context.getWidget()+".addItem(" +parentWidget +","+ label +");");
			}
			setItemAttributes(out, context, item);
			context.itemStack.addFirst(item);
		}
		
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
			String open = context.readChildProperty("open");
			if (!StringUtils.isEmpty(open))
			{
				if(Boolean.parseBoolean(open))
				{
					out.println(item + ".open(" + Boolean.parseBoolean(open) + ");");					
				} else
				{
					out.println(item + ".close(" + Boolean.parseBoolean(open) + ");");
				}
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
				out.println(item + ".addClassName(" + EscapeUtils.quote(styleName) + ");");
			}
		}				
	}
	
	@Override
    public MenuContext instantiateContext()
    {
	    return new MenuContext();
    }
}