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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ConfirmFactory extends WidgetFactory<Confirm>
{

	@Override
	protected Confirm instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new Confirm();
	}

	@Override
	protected void processAttributes(Confirm widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String title = element.getAttribute("_title");
		if (title != null && title.length() > 0)
		{
			widget.setTitle(title);
		}

		String message = element.getAttribute("_message");
		if (message != null && message.length() > 0)
		{
			widget.setMessage(ScreenFactory.getInstance().getDeclaredMessage(message));
		}

		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
	}
	
	@Override
	protected void processEvents(Confirm widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		CancelEvtBind.bindEvent(element, widget);
		OkEvtBind.bindEvent(element, widget);
	}
}
