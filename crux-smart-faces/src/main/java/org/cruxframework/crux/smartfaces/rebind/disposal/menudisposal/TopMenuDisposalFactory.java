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

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.TopMenuDisposal;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.TopMenuDisposal.TopDisposalMenuType;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal.TopMenuDisposalFactory.DisposalLayoutContext;

import com.google.gwt.core.client.GWT;


@DeclarativeFactory(library=Constants.LIBRARY_NAME,id="topMenuDisposal",targetWidget=TopMenuDisposal.class, description="A component to define the page's layout. It contains a header, a interactive menu, a content panel and a footer.")
@TagAttributesDeclaration(
		@TagAttributeDeclaration(value="historyControlPrefix",defaultValue="view")
		)
@TagChildren({
	@TagChild(TopMenuDisposalFactory.ViewProcessor.class),
	@TagChild(TopMenuDisposalFactory.LayoutSmallHeaderProcessor.class),
	@TagChild(TopMenuDisposalFactory.LayoutHeaderProcessor.class),
	@TagChild(TopMenuDisposalFactory.LayoutFooterProcessor.class),
	@TagChild(TopMenuDisposalFactory.MenuProcessor.class)
})
public class TopMenuDisposalFactory extends WidgetCreator<DisposalLayoutContext> 
{
	@Override
	public DisposalLayoutContext instantiateContext()
	{
		return new DisposalLayoutContext();
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="1", tagName="view")
    @TagAttributesDeclaration({
    	@TagAttributeDeclaration(value="name", required=true)
    })
    public static class ViewProcessor extends WidgetChildProcessor<DisposalLayoutContext>
    {
    	@Override
    	public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
    	{
    		out.println("if(!"+context.getWidget()+".getCurrentHistoryItem().contains(\"!\"))");
    		out.println(context.getWidget()+".showView("+EscapeUtils.quote(context.readChildProperty("name"))+");");
    	}
    }
		
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="header")
	@TagChildren({
		@TagChild(value=TopMenuDisposalFactory.HeaderProcessor.class)
	})
	public static class LayoutHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="smallHeader")
	@TagChildren({
		@TagChild(value=TopMenuDisposalFactory.SmallHeaderProcessor.class)
	})
	public static class LayoutSmallHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	
    @Override
	public void instantiateWidget(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
	{
    	String className = getWidgetClassName();
    	out.println("final "+className + " " + context.getWidget()+" = "+GWT.class.getCanonicalName()+".create("+className+".class);");
    	out.print(context.getWidget()+".setHistoryControlPrefix("+EscapeUtils.quote(context.readChildProperty("historyControlPrefix"))+");");
	}
	
	@TagConstraints(maxOccurs="1",minOccurs="0",tagName="mainMenu")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="menuType", type=TopDisposalMenuType.class, defaultValue="HORIZONTAL_DROPDOWN")
	})
	@TagChildren({
		@TagChild(TopMenuDisposalFactory.MenuItemProcessor.class)
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
				menuType = "HORIZONTAL_DROPDOWN";
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
	
	
	@TagConstraints(maxOccurs="unbounded", minOccurs="0",tagName="menuItem")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="targetView",required=false,description="Defines the target view that will be displayed on clicking it"),
		@TagAttributeDeclaration(value="label", required=true,description="Defines the label that will be displayed")
	})
	@TagChildren({
		@TagChild(TopMenuDisposalFactory.MenuItemProcessor.class)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<DisposalLayoutContext> implements HasPostProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String label = EscapeUtils.quote(context.readChildProperty("label"));
			String view = EscapeUtils.quote(context.readChildProperty("targetView"));
			String menuItem = getWidgetCreator().createVariableName("menuItem");
			
			context.currentItem = menuItem;
			
			if(context.itemStack.size() == 1)
			{
				out.println(MenuItem.class.getCanonicalName() + " "+context.currentItem+" = " + context.menu + ".addItem("+label+");");
			}else
			{
				String parent = context.itemStack.getFirst();
				out.println(MenuItem.class.getCanonicalName() + " "+context.currentItem+" = " + context.menu + ".addItem("+parent+","+label+");");
			}
			
			context.itemStack.addFirst(context.currentItem);
			if(!view.isEmpty() && !view.equals("\"\""))
			{
				out.println(menuItem+".addSelectHandler(new " + SelectHandler.class.getCanonicalName() + "(){public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");
				out.println(context.getWidget()+".showView("+view+");} });");
			}
		}
		
		@Override
		public void postProcessChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			context.itemStack.removeFirst();			
		}
	}
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="footer")
	@TagChildren({
		@TagChild(value=TopMenuDisposalFactory.FooterProcessor.class)
	})
	public static class LayoutFooterProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(maxOccurs="unbounded", minOccurs="0", type=AnyWidget.class)
	public static class HeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget()+".addHeaderContent("+widget+");");
		}
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
