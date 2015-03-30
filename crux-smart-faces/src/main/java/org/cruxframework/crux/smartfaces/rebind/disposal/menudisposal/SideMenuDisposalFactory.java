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
package org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal;

import java.util.LinkedList;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.event.SelectEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.SideMenuDisposal;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.SideMenuDisposal.MenuPosition;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal.SideMenuDisposalFactory.DisposalLayoutContext;


@DeclarativeFactory(library=Constants.LIBRARY_NAME,id="sideMenuDisposal", 
					targetWidget=SideMenuDisposal.class, 
					description="A component to define the page's layout. It contains a header, a interactive menu, a content panel and a footer.")
@TagAttributes({
	@TagAttribute(value="menuPositioning", type=MenuPosition.class, defaultValue="LEFT", 
				  description="The position where the menu will be placed"),
	@TagAttribute(value="historyControlPrefix",defaultValue="view", 
				  description="The name of the token to be used to identify the history state. This token will be used as part of the URL to the view showed by this panel.")
})
@TagChildren({
	@TagChild(SideMenuDisposalFactory.DisposalChildrenProcessor.class)
})
public class SideMenuDisposalFactory extends WidgetCreator<DisposalLayoutContext> 
{
	@Override
	public DisposalLayoutContext instantiateContext()
	{
		return new DisposalLayoutContext();
	}

	@TagConstraints(minOccurs="5", maxOccurs="5")
	@TagChildren({
		@TagChild(SideMenuDisposalFactory.ViewProcessor.class),
		@TagChild(SideMenuDisposalFactory.LayoutSmallHeaderProcessor.class),
		@TagChild(SideMenuDisposalFactory.LayoutLargeHeaderProcessor.class),
		@TagChild(SideMenuDisposalFactory.LayoutFooterProcessor.class),
		@TagChild(SideMenuDisposalFactory.MenuProcessor.class)
	})
	public static class DisposalChildrenProcessor extends ChoiceChildProcessor<DisposalLayoutContext>{}
	
    @TagConstraints(minOccurs="1", maxOccurs="1", tagName="view", 
    				description="A view to be rendered into this view container.")
    @TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", description="The view identifier."),
    	@TagAttributeDeclaration(value="name", required=true, description="The name of the view.")
    })
    public static class ViewProcessor extends WidgetChildProcessor<DisposalLayoutContext>
    {
    	@Override
    	public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
    	{
    		String viewId = context.readChildProperty("id");
    		String viewName = context.readChildProperty("name");
    		
    		if (StringUtils.isEmpty(viewId))
    		{
    			viewId = viewName;
    		}
    		out.println("if(!"+context.getWidget()+".isHistoryTarget())");
    		out.println(context.getWidget()+".showView("+EscapeUtils.quote(viewName)+", "+EscapeUtils.quote(viewId)+");");
    	}
    }
	
	
	@TagConstraints(minOccurs="1",maxOccurs="1", tagName="smallHeader", 
					description="The header section to be used on small devices.")
	@TagChildren({
		@TagChild(value=SideMenuDisposalFactory.SmallHeaderProcessor.class)
	})
	public static class LayoutSmallHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(minOccurs="1",maxOccurs="1", tagName="largeHeader", 
					description="The header section to be used on large devices.")
	@TagChildren({
		@TagChild(value=SideMenuDisposalFactory.LargeHeaderProcessor.class)
	})
	public static class LayoutLargeHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	static enum SideDisposalMenuType
	{
		VERTICAL_TREE, VERTICAL_SLIDE, VERTICAL_ACCORDION, VERTICAL_DROPDOWN;
	}

	@TagConstraints(maxOccurs="1",minOccurs="1",tagName="mainMenu", 
					description="The menu section of this component.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="menuType", type=SideDisposalMenuType.class, defaultValue="VERTICAL_DROPDOWN", 
					description="The type of the menu to be displayed.")
	})
	@TagChildren({
		@TagChild(SideMenuDisposalFactory.MenuItemProcessor.class)
	})
	public static class MenuProcessor extends WidgetChildProcessor<DisposalLayoutContext> implements HasPostProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String menu = getWidgetCreator().createVariableName("menuWidget");
			String menuType = context.readChildProperty("menuType");
			
			if(menuType.isEmpty())
			{
				menuType = SideDisposalMenuType.VERTICAL_DROPDOWN.name();
			}
			
			out.println(Menu.class.getCanonicalName() + " " + menu + " = new "+Menu.class.getCanonicalName()+"("+LargeType.class.getCanonicalName()+"."+menuType+"," + SmallType.class.getCanonicalName()+".VERTICAL_ACCORDION);");
			context.menu = menu;
			context.itemStack.addFirst(menu);
			
		}

		@Override
		public void postProcessChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			out.println(context.getWidget()+".setMenu("+context.menu +");");			
		}
	}
    
    @TagConstraints(maxOccurs="unbounded", minOccurs="0",tagName="menuItem", 
    				description="A menu item.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="targetView",required=false,description="Defines the target view that will be displayed on clicking it"),
		@TagAttributeDeclaration(value="label", required=true, description="Defines the label that will be displayed", supportsI18N=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onSelect", description="Event fired when user select this menu entry")
	})
	@TagChildren({
		@TagChild(SideMenuDisposalFactory.MenuItemProcessor.class)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<DisposalLayoutContext> implements HasPostProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String label = getWidgetCreator().getDeclaredMessage(context.readChildProperty("label"));
			String view = context.readChildProperty("targetView");
			String menuItem = getWidgetCreator().createVariableName("menuItem");
			
			context.currentItem = menuItem;
			
			if(context.itemStack.size() == 1)
			{
				out.println(MenuItem.class.getCanonicalName() + " "+context.currentItem+" = " + context.menu + ".addItem("+label+");");
			}
			else
			{
				String parent = context.itemStack.getFirst();
				out.println(MenuItem.class.getCanonicalName() + " "+context.currentItem+" = " + context.menu + ".addItem("+parent+","+label+");");
			}
			
			context.itemStack.addFirst(context.currentItem);
			
			String onSelectEvent = context.readChildProperty("onSelect");
			if (!StringUtils.isEmpty(onSelectEvent))
			{
				new SelectEvtBind(getWidgetCreator()).processEvent(out, onSelectEvent, context.currentItem, context.getWidgetId());
			}
			
			if(!StringUtils.isEmpty(view))
			{
				out.println(menuItem+".setValue("+EscapeUtils.quote(view) +");");
			}
		}
		
		@Override
		public void postProcessChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			context.itemStack.removeFirst();			
		}
	}
	
	@TagConstraints(minOccurs="1",maxOccurs="1", tagName="footer", 
					description="The footer section of this container.")
	@TagChildren({
		@TagChild(value=SideMenuDisposalFactory.FooterProcessor.class)
	})
	public static class LayoutFooterProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(maxOccurs="unbounded", minOccurs="0", type=AnyWidget.class)
	public static class SmallHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget()+".addSmallHeaderContent("+widget+");");
		}
	}
	
	
	@TagConstraints(maxOccurs="unbounded", minOccurs="0", type=AnyWidget.class)
	public static class LargeHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget()+".addLargeHeaderContent("+widget+");");
		}
	}
	
	@TagConstraints(maxOccurs="unbounded", minOccurs="0", type=AnyWidget.class)
	public static class FooterProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget()+".addFooterContent("+widget+");");
		}
	}
	
	class DisposalLayoutContext extends WidgetCreatorContext
    {
    	String menu;
    	String currentItem;
    	LinkedList<String> itemStack = new LinkedList<String>();
    }
}
