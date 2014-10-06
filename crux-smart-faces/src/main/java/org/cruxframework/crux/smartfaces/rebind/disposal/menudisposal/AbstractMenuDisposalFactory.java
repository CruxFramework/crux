package org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal;

import java.util.LinkedList;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.event.SelectEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.menu.Type;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.rebind.disposal.menudisposal.AbstractMenuDisposalFactory.DisposalLayoutContext;

import com.google.gwt.core.client.GWT;

@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="historyControlPrefix",defaultValue="view")
})
@TagChildren({
	@TagChild(AbstractMenuDisposalFactory.ViewProcessor.class),
	@TagChild(AbstractMenuDisposalFactory.LayoutSmallHeaderProcessor.class),
	@TagChild(AbstractMenuDisposalFactory.LayoutLargeHeaderProcessor.class),
	@TagChild(AbstractMenuDisposalFactory.LayoutFooterProcessor.class),
	@TagChild(AbstractMenuDisposalFactory.MenuProcessor.class)
})
public abstract class AbstractMenuDisposalFactory extends WidgetCreator<DisposalLayoutContext>
{
	@Override
	public DisposalLayoutContext instantiateContext()
	{
		DisposalLayoutContext context = new DisposalLayoutContext();
		context.defaultMenuType = getDefaultMenuType();
		return context;
	}
	
	abstract String getDefaultMenuType();
	
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
	
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="smallHeader")
	@TagChildren({
		@TagChild(value=AbstractMenuDisposalFactory.SmallHeaderProcessor.class)
	})
	public static class LayoutSmallHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
	{
	}
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="largeHeader")
	@TagChildren({
		@TagChild(value=AbstractMenuDisposalFactory.LargeHeaderProcessor.class)
	})
	public static class LayoutLargeHeaderProcessor extends WidgetChildProcessor<DisposalLayoutContext>
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
		@TagAttributeDeclaration(value="menuType", type=Type.class)
	})
	@TagChildren({
		@TagChild(AbstractMenuDisposalFactory.MenuItemProcessor.class)
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
				menuType = context.defaultMenuType;
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
		@TagAttributeDeclaration(value="label", required=true, description="Defines the label that will be displayed", supportsI18N=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onSelect", description="Event fired when user select this menu entry")
	})
	@TagChildren({
		@TagChild(AbstractMenuDisposalFactory.MenuItemProcessor.class)
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
	
	@TagConstraints(minOccurs="0",maxOccurs="1", tagName="footer")
	@TagChildren({
		@TagChild(value=AbstractMenuDisposalFactory.FooterProcessor.class)
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
    	String defaultMenuType;
    	LinkedList<String> itemStack = new LinkedList<String>();
    }
}
