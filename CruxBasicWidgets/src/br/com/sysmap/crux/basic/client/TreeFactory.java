/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.basic.client;

import java.util.LinkedList;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.HasPostProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasOpenHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a TreeFactory DeclarativeFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="tree", library="gwt")
public class TreeFactory extends WidgetFactory<Tree> 
       implements HasAnimationFactory<Tree>, HasAllFocusHandlersFactory<Tree>,
                  HasOpenHandlersFactory<Tree>, HasCloseHandlersFactory<Tree>, 
                  HasAllMouseHandlersFactory<Tree>, HasAllKeyHandlersFactory<Tree>,
                  HasSelectionHandlersFactory<Tree>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	@Override
	public Tree instantiateWidget(Element element, String widgetId) 
	{
		Event eventLoadImage = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<Tree> loadEvent = new LoadImagesEvent<Tree>(widgetId);
			TreeImages treeImages = (TreeImages) Events.callEvent(eventLoadImage, loadEvent);

			String useLeafImagesStr = element.getAttribute("_useLeafImages");
			boolean useLeafImages = true;
			if (useLeafImagesStr != null && useLeafImagesStr.length() > 0)
			{
				useLeafImages = (Boolean.parseBoolean(useLeafImagesStr));
			}
			
			return new Tree(treeImages, useLeafImages);
		}
		return new Tree();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="useLeafImages", type=Boolean.class),
		@TagAttributeDeclaration(value="openSelectedItem", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<Tree> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		Tree widget = context.getWidget();
		
		String openSelectedItem = element.getAttribute("_openSelectedItem");
		if (openSelectedItem != null && openSelectedItem.length() > 0)
		{
			if(Boolean.parseBoolean(openSelectedItem))
			{
				widget.ensureSelectedItemVisible();
			}
		}
	}

	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<Tree> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(TreeItemProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<Tree> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public static class TreeItemProcessor extends WidgetChildProcessor<Tree> implements HasPostProcessor<Tree>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="state", type=Boolean.class),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		@TagChildren({
			@TagChild(TreeCaptionProcessor.class),
			@TagChild(TreeItemProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException 
		{
			LinkedList<TreeItem> itemStack = new LinkedList<TreeItem>();
			context.setAttribute("itemStack", itemStack);
		}
		
		@SuppressWarnings("unchecked")
		public void postProcessChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException 
		{
			LinkedList<TreeItem> itemStack = (LinkedList<TreeItem>) context.getAttribute("itemStack");
			itemStack.removeFirst();
		}
	}
	
	public static class TreeCaptionProcessor extends ChoiceChildProcessor<Tree>
	{
		@Override
		@TagChildren({
			@TagChild(TextCaptionProcessor.class),
			@TagChild(WidgetCaptionProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="textTitle", type=String.class)
	public static class TextCaptionProcessor extends WidgetChildProcessor<Tree>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException 
		{
			String textCaption = context.getChildElement().getInnerHTML();
			LinkedList<TreeItem> itemStack = (LinkedList<TreeItem>) context.getAttribute("itemStack");
			
			TreeItem parent = itemStack.peek();
			TreeItem currentItem;
			if (parent == null)
			{
				currentItem = context.getRootWidget().addItem(textCaption);
			}
			else
			{
				currentItem = parent.addItem(textCaption);
			}
			itemStack.addFirst(currentItem);
		}
	}	

	@TagChildAttributes(tagName="widgetTitle")
	public static class WidgetCaptionProcessor extends WidgetChildProcessor<Tree>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetCaptionWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException {}
	}	


	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetCaptionWidgetProcessor extends WidgetChildProcessor<Tree>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(WidgetChildProcessorContext<Tree> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			Widget child = createChildWidget(childElement, childElement.getId());
			LinkedList<TreeItem> itemStack = (LinkedList<TreeItem>) context.getAttribute("itemStack");

			TreeItem parent = itemStack.peek();
			TreeItem currentItem;
			if (parent == null)
			{
				currentItem = context.getRootWidget().addItem(child);
			}
			else
			{
				currentItem = parent.addItem(child);
			}
			itemStack.addFirst(currentItem);
		}
	}
}
