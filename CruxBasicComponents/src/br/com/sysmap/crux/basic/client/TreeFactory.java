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

import br.com.sysmap.crux.core.client.component.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.WidgetFactory;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.OpenEvtBind;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a TreeFactory WidgetFactory
 * @author Thiago Bustamante
 */
public class TreeFactory extends WidgetFactory<Tree> implements HasWidgetsFactory<Tree>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	protected Tree instantiateWidget(Element element, String widgetId) 
	{
		Event eventLoadImage = EvtBind.getWidgetEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<Tree> loadEvent = new LoadImagesEvent<Tree>(widgetId);
			TreeImages treeImages = (TreeImages) EventFactory.callEvent(eventLoadImage, loadEvent);

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
	protected void processAttributes(Tree widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processAttributes(widget, element, widgetId);
		
		String openSelectedItem = element.getAttribute("_openSelectedItem");
		if (openSelectedItem != null && openSelectedItem.length() > 0)
		{
			if(Boolean.parseBoolean(openSelectedItem))
			{
				widget.ensureSelectedItemVisible();
			}
		}
		
		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			widget.setTabIndex(Integer.parseInt(tabIndex));
		}

		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			widget.setAccessKey(accessKey.charAt(0));
		}
		
		String focus = element.getAttribute("_focus");
		if (focus != null && focus.trim().length() > 0)
		{
			widget.setFocus(Boolean.parseBoolean(focus));
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		processTreeItens(widget, element);
	}
	
	@Override
	protected void processEvents(Tree widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		FocusEvtBind.bindEvents(element, widget);
		OpenEvtBind.bindEvent(element, widget);
		CloseEvtBind.bindEvent(element, widget);
		MouseEvtBind.bindEvents(element, widget);
		KeyEvtBind.bindEvents(element, widget);
		SelectionEvtBind.bindEvent(element, widget);
	}

	
	/**
	 * Populate the tree with declared items
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	protected void processTreeItens(Tree widget, Element element) throws InterfaceConfigException
	{
		processTreeItens(widget, element, null);
	}
	
	protected void processTreeItens(Tree widget, Element element, TreeItem parent) throws InterfaceConfigException
	{
		List<Element> itensCandidates = ensureChildrenSpans(element, true);
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			TreeItem item = processTreeItem(widget, e, parent);
			processTreeItens(widget, e, item);
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
