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
package br.com.sysmap.crux.advanced.client.dialog;

import br.com.sysmap.crux.advanced.client.event.dialog.CancelEvtBind;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvtBind;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="confirm", library="adv")
public class ConfirmFactory extends WidgetFactory<Confirm> 
       implements HasAnimationFactory<Confirm>
{

	@Override
	public Confirm instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new Confirm();
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="title", supportsI18N=true),
		@TagAttribute(value="message", supportsI18N=true),
		@TagAttribute(value="okButtonText", supportsI18N=true),
		@TagAttribute(value="cancelButtonText", supportsI18N=true)
	})
	public void processAttributes(WidgetFactoryContext<Confirm> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagEvents({
		@TagEvent(CancelEvtBind.class),
		@TagEvent(OkEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<Confirm> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
}
