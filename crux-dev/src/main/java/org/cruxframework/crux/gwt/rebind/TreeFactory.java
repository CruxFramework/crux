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

import java.util.LinkedList;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasOpenHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
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
import org.cruxframework.crux.gwt.client.LoadImagesEvent;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Tree.Resources;
import com.google.gwt.user.client.ui.TreeItem;


class TreeContext extends WidgetCreatorContext
{
	LinkedList<String> itemStack = new LinkedList<String>();
}

/**
 * A factory for Tree widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tree", library="gwt", targetWidget=Tree.class)
@TagAttributes({
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="openSelectedItem", type=Boolean.class, processor=TreeFactory.OpenSelectedItemAttributeParser.class),
	@TagAttribute(value="focus", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="useLeafImages", type=Boolean.class)
})
@TagEventsDeclaration({
	@TagEventDeclaration("onLoadImage")
})
@TagChildren({
	@TagChild(TreeFactory.TreeItemProcessor.class)
})
public class TreeFactory extends WidgetCreator<TreeContext> 
       implements HasAnimationFactory<TreeContext>, HasAllFocusHandlersFactory<TreeContext>,
                  HasOpenHandlersFactory<TreeContext>, HasCloseHandlersFactory<TreeContext>, 
                  HasAllMouseHandlersFactory<TreeContext>, HasAllKeyHandlersFactory<TreeContext>,
                  HasSelectionHandlersFactory<TreeContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, TreeContext context) throws CruxGeneratorException
	{
		String className = Tree.class.getCanonicalName();
		
		String eventLoadImage = context.readWidgetProperty("onLoadImage");
		
		if (!StringUtils.isEmpty(eventLoadImage))
		{
			String treeImages = createVariableName("treeImages");
			
			out.println(Resources.class.getCanonicalName()+" "+treeImages+
					" = ("+Resources.class.getCanonicalName()+") ");
			EvtProcessor.printEvtCall(out, eventLoadImage, "onLoadImage", LoadImagesEvent.class.getCanonicalName()+"<"+className+">", 
					" new "+LoadImagesEvent.class.getCanonicalName()+"<"+className+">("+EscapeUtils.quote(context.getWidgetId())+")", this);

			String useLeafImagesStr = context.readWidgetProperty("useLeafImages");
			boolean useLeafImages = true;
			if (useLeafImagesStr != null && useLeafImagesStr.length() > 0)
			{
				useLeafImages = (Boolean.parseBoolean(useLeafImagesStr));
			}
			
			out.println(className + " " + context.getWidget()+" = new "+className+"("+treeImages+", "+useLeafImages+");");
		}
		else
		{
			out.println(className + " " + context.getWidget()+" = new "+className+"();");
		}
	}
	
	@Override
	public void processAttributes(SourcePrinter out, TreeContext context) throws CruxGeneratorException 
	{
		super.processAttributes(out, context);
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 */
	public static class OpenSelectedItemAttributeParser extends AttributeProcessor<TreeContext>
	{
		public OpenSelectedItemAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, TreeContext context, String propertyValue) 
		{
			if(Boolean.parseBoolean(propertyValue))
			{
				String widget = context.getWidget();
				out.println(widget+".ensureSelectedItemVisible();");
			}
		}
	}
	
	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="state", type=Boolean.class),
		@TagAttributeDeclaration(value="selected", type=Boolean.class)
	})
	@TagChildren({
		@TagChild(TreeCaptionProcessor.class),
		@TagChild(TreeItemProcessor.class)
	})
	public static class TreeItemProcessor extends WidgetChildProcessor<TreeContext> implements HasPostProcessor<TreeContext>
	{
		public void postProcessChildren(SourcePrinter out, TreeContext context) throws CruxGeneratorException 
		{
			context.itemStack.removeFirst();
		}
	}
	
	@TagChildren({
		@TagChild(TextCaptionProcessor.class),
		@TagChild(WidgetCaptionProcessor.class)
	})
	public static class TreeCaptionProcessor extends ChoiceChildProcessor<TreeContext> {}
	
	@TagConstraints(tagName="textTitle", type=String.class)
	public static class TextCaptionProcessor extends WidgetChildProcessor<TreeContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TreeContext context) throws CruxGeneratorException 
		{
			String textCaption = getWidgetCreator().ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false);
			
			String parent = context.itemStack.peek();
			String rootWidget = context.getWidget();
			String currentItem = getWidgetCreator().createVariableName("item");
			out.println(TreeItem.class.getCanonicalName()+" "+currentItem+";");
			
			if (parent == null)
			{
				out.println(currentItem+" = "+rootWidget+".addItem("+EscapeUtils.quote(textCaption)+");");
			}
			else
			{
				out.println(currentItem+" = "+parent+".addItem("+EscapeUtils.quote(textCaption)+");");
			}
			context.itemStack.addFirst(currentItem);
		}
	}	

	@TagConstraints(tagName="widgetTitle")
	@TagChildren({
		@TagChild(WidgetCaptionWidgetProcessor.class)
	})
	public static class WidgetCaptionProcessor extends WidgetChildProcessor<TreeContext> {}	

	@TagConstraints(type=AnyWidget.class)
	public static class WidgetCaptionWidgetProcessor extends WidgetChildProcessor<TreeContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TreeContext context) throws CruxGeneratorException 
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}

			String parent = context.itemStack.peek();
			String currentItem = getWidgetCreator().createVariableName("item");
			out.println(TreeItem.class.getCanonicalName()+" "+currentItem+";");
			if (parent == null)
			{
				String rootWidget = context.getWidget();
				out.println(currentItem+" = "+rootWidget+".addItem("+child+");");
			}
			else
			{
				out.println(currentItem+" = "+parent+".addItem("+child+");");
			}
			context.itemStack.addFirst(currentItem);
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}

	@Override
    public TreeContext instantiateContext()
    {
	    return new TreeContext();
    }
}
