package org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal;

import java.util.LinkedList;

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
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.SideMenuDisposal;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.SideMenuDisposal.MenuPosition;
import org.cruxframework.crux.smartfaces.client.disposal.menudisposal.SideMenuDisposal.SideDisposalMenuType;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal.SideMenuDisposalFactory.DisposalLayoutContext;

import com.google.gwt.core.client.GWT;


@DeclarativeFactory(library=Constants.LIBRARY_NAME,id="sideMenuDisposal",targetWidget=SideMenuDisposal.class,description="A component to define the page's layout. It contains a header, a interactive menu, a content panel and a footer.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="menuPositioning",type=MenuPosition.class,defaultValue="LEFT"),
	@TagAttributeDeclaration(value="historyControlPrefix",defaultValue="view")
})
@TagChildren({
	@TagChild(SideMenuDisposalFactory.ViewProcessor.class),
	@TagChild(SideMenuDisposalFactory.LayoutSmallHeaderProcessor.class),
	@TagChild(SideMenuDisposalFactory.LayoutHeaderProcessor.class),
	@TagChild(SideMenuDisposalFactory.LayoutFooterProcessor.class),
	@TagChild(SideMenuDisposalFactory.MenuProcessor.class)
})
public class SideMenuDisposalFactory extends WidgetCreator<DisposalLayoutContext> 
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
    		out.println(context.getWidget()+".showView("+EscapeUtils.quote(context.readChildProperty("name"))+");");
    	}
    }
	
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="smallHeader")
	@TagChildren({
		@TagChild(value=SideMenuDisposalFactory.SmallHeaderProcessor.class)
	})
	public static class LayoutSmallHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="header")
	@TagChildren({
		@TagChild(value=SideMenuDisposalFactory.HeaderProcessor.class)
	})
	public static class LayoutHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
    @Override
	public void instantiateWidget(SourcePrinter out, DisposalLayoutContext context) throws CruxGeneratorException
	{
    	String className = getWidgetClassName();
    	out.println("final "+className + " " + context.getWidget()+" = "+GWT.class.getCanonicalName()+".create("+className+".class);");
    	String menuPositioning = context.readChildProperty("menuPositioning");
    	
    	if(menuPositioning.isEmpty())
    	{
    		menuPositioning = "LEFT";
    	}
    	
    	out.println(context.getWidget()+".setMenuPositioning("+MenuPosition.class.getCanonicalName()+"."+menuPositioning+");");
    	out.print(context.getWidget()+".setHistoryControlPrefix("+EscapeUtils.quote(context.readChildProperty("historyControlPrefix"))+");");
	}
	
	@TagConstraints(maxOccurs="1",minOccurs="0",tagName="mainMenu")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="menuType", type=SideDisposalMenuType.class, defaultValue="VERTICAL_DROPDOWN")
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
				menuType = "VERTICAL_DROPDOWN";
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
		@TagChild(SideMenuDisposalFactory.MenuItemProcessor.class)
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
