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

import java.util.List;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
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
@DeclarativeFactory(id="tree", library="bas")
public class TreeFactory extends WidgetFactory<Tree> 
       implements HasWidgetsFactory<Tree>, HasAnimationFactory<Tree>, HasAllFocusHandlersFactory<Tree>,
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
		@TagAttribute(value="useLeafImages", type=Boolean.class, autoProcess=false),
		@TagAttribute(value="openSelectedItem", type=Boolean.class, autoProcess=false),
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class)
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
		// TODO Auto-generated method stub
		super.processEvents(context);
	}
	
	@Override
	public void processChildren(WidgetFactoryContext<Tree> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		Tree widget = context.getWidget();

		List<Element> itens = ensureChildrenSpans(element, true);
		for (Element e : itens)
		{
			processTreeItens(widget, e, null);
		}
	}
	
	protected void processTreeItens(Tree widget, Element element, TreeItem parent) throws InterfaceConfigException
	{
		List<Element> itens = ensureChildrenSpans(element, false);
		TreeItem item = processTreeItem(widget, itens.get(0), parent);
		
		processItemAttributes(element, item);

		for (int i=1; i<itens.size(); i++)
		{
			Element e = (Element)itens.get(i);
			processTreeItens(widget, e, item);
		}
	}

	private void processItemAttributes(Element element, TreeItem item)
	{
		String selected = element.getAttribute("_selected");
		if (selected != null && selected.trim().length() > 0)
		{
			item.setSelected(Boolean.parseBoolean(selected));
		}
		
		String state = element.getAttribute("_state");
		if (selected != null && selected.trim().length() > 0)
		{
			item.setState(Boolean.parseBoolean(state));
		}
	}
	
	protected TreeItem processTreeItem(Tree widget, Element e, TreeItem parent) throws InterfaceConfigException
	{
		String type = e.getAttribute("_type");
		if (type != null && type.length() > 0)
		{
			if (parent != null)
			{
				return parent.addItem(createChildWidget(e, e.getId()));
			}
			else
			{
				return widget.addItem(createChildWidget(e, e.getId()));
			}
		}
		else
		{
			String text = e.getAttribute("_text");
			if (text == null || text.length() == 0)
			{
				throw new InterfaceConfigException(messages.treeInvalidTreeItem(e.getId()));
			}
			if (parent != null)
			{
				return parent.addItem(text);
			}
			else
			{
				return widget.addItem(text);
			}
		}
	}

	public void add(Tree parent, Widget child, Element parentElement, Element childElement) 
	{
		// Does not need to add the child because it was already attached in processAttributes method
	}	
}
