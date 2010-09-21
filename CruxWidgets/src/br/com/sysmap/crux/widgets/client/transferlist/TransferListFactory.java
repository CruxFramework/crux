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
package br.com.sysmap.crux.widgets.client.transferlist;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.gwt.client.CompositeFactory;
import br.com.sysmap.crux.widgets.client.event.moveitem.BeforeMoveItemsEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Transfer List widget
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="transferList", library="widgets")
public class TransferListFactory extends CompositeFactory<TransferList>
{
	@Override
	public TransferList instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new TransferList();
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="leftToRightButtonText", supportsI18N=true),
		@TagAttribute(value="rightToLeftButtonText", supportsI18N=true),
		@TagAttribute(value="leftListLabel", supportsI18N=true),
		@TagAttribute(value="rightListLabel", supportsI18N=true),
		@TagAttribute(value="visibleItemCount", type=Integer.class),
		@TagAttribute(value="multiTransferFromLeft", type=Boolean.class, defaultValue="true"),
		@TagAttribute(value="multiTransferFromRight", type=Boolean.class, defaultValue="true")
	})
	public void processAttributes(WidgetFactoryContext<TransferList> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	
	@Override
	@TagEvents({
		@TagEvent(BeforeMoveItemsEvtBind.class)
	})
	public void processEvents(br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext<TransferList> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
}