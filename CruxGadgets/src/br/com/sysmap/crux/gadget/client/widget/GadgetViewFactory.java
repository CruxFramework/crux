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
package br.com.sysmap.crux.gadget.client.widget;

import java.util.List;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.HasWidgetsHandler;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.gwt.client.AbstractHTMLPanelFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="gadgetView", library="gadget")
public class GadgetViewFactory extends AbstractHTMLPanelFactory<GadgetView>
{
	@Override
	public GadgetView instantiateWidget(Element element, String widgetId) 
	{
		GadgetView ret = new GadgetView("");
		List<Node> children = extractChildrenInReverseOrder(element);
		for (Node node : children)
		{
			ret.getElement().appendChild(node);
		}
		HasWidgetsHandler.handleWidgetElement(ret, widgetId, "gadget_gadgetView");

		return ret;
	}

	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetFactoryContext<GadgetView> context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=AnyTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<GadgetView> {}
}
