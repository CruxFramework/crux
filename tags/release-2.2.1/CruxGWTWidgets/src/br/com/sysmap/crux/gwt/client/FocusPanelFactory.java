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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.BlurEvtBind;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseMoveEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseOutEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseOverEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseUpEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseWheelEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Represents a FocusPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="focusPanel", library="gwt")
public class FocusPanelFactory extends PanelFactory<FocusPanel>
{
	@Override
	@TagEvents({
		@TagEvent(ClickEvtBind.class),
		@TagEvent(FocusEvtBind.class),
		@TagEvent(BlurEvtBind.class),
		@TagEvent(KeyUpEvtBind.class),
		@TagEvent(KeyPressEvtBind.class),
		@TagEvent(KeyDownEvtBind.class),
		@TagEvent(MouseDownEvtBind.class),
		@TagEvent(MouseUpEvtBind.class),
		@TagEvent(MouseOverEvtBind.class),
		@TagEvent(MouseOutEvtBind.class),
		@TagEvent(MouseMoveEvtBind.class),
		@TagEvent(MouseWheelEvtBind.class)
	})	
	public void processEvents(WidgetFactoryContext<FocusPanel> context) throws InterfaceConfigException 
	{
		super.processEvents(context);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<FocusPanel> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
	
	@Override
	public FocusPanel instantiateWidget(Element element, String widgetId) 
	{
		return new FocusPanel();
	}
}