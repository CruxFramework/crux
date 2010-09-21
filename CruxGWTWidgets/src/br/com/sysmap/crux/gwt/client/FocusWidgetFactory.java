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

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
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
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.user.client.ui.FocusWidget;

/**
 * This is the base factory class for widgets that can receive focus. 
 * 
 * @author Thiago Bustamante
 *
 */
public abstract class FocusWidgetFactory <T extends FocusWidget> extends WidgetFactory<T> 
{
	/**
	 * Process widget attributes
	 * @throws InterfaceConfigException 
	 */
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="tabIndex", type=Integer.class),
		@TagAttributeDeclaration(value="enabled", type=Boolean.class),
		@TagAttributeDeclaration(value="accessKey", type=Character.class),
		@TagAttributeDeclaration(value="focus", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		String tabIndex = context.readWidgetProperty("tabIndex");
		if (tabIndex != null && tabIndex.length() > 0){
			context.getWidget().setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = context.readWidgetProperty("enabled");
		if (enabled != null && enabled.length() > 0){
			context.getWidget().setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = context.readWidgetProperty("accessKey");
		if (accessKey != null && accessKey.length() > 0){
			context.getWidget().setAccessKey(accessKey.charAt(0));
		}
		String focus = context.readWidgetProperty("focus");
		if (focus != null && focus.length() > 0){
			context.getWidget().setFocus(Boolean.parseBoolean(focus));
		}
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onClick"),
		@TagEventDeclaration("onFocus"),
		@TagEventDeclaration("onBlur"),
		@TagEventDeclaration("onKeyPress"),
		@TagEventDeclaration("onKeyUp"),
		@TagEventDeclaration("onKeyDown"),
		@TagEventDeclaration("onMouseDown"),
		@TagEventDeclaration("onMouseUp"),
		@TagEventDeclaration("onMouseOver"),
		@TagEventDeclaration("onMouseOut"),
		@TagEventDeclaration("onMouseMove"),
		@TagEventDeclaration("onMouseWheel")
	})
	public void processEvents(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processEvents(context);
		new ClickEvtBind().bindEvent(context.getElement(), context.getWidget());
		new FocusEvtBind().bindEvent(context.getElement(), context.getWidget());
		new BlurEvtBind().bindEvent(context.getElement(), context.getWidget());
		new KeyUpEvtBind().bindEvent(context.getElement(), context.getWidget());
		new KeyPressEvtBind().bindEvent(context.getElement(), context.getWidget());
		new KeyDownEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseDownEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseUpEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseOverEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseOutEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseMoveEvtBind().bindEvent(context.getElement(), context.getWidget());
		new MouseWheelEvtBind().bindEvent(context.getElement(), context.getWidget());
	}
}