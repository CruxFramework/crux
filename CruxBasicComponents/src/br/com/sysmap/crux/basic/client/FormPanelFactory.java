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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

/**
 * Represents a FormPanelFactory.
 * @author Thiago Bustamante
 */
public class FormPanelFactory extends SimplePanelFactory
{
	
	@Override
	protected void processAttributes(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processAttributes(widget, element, widgetId);
		FormPanel formPanel = (FormPanel)widget;
		
		String method = element.getAttribute("_method");
		if (method != null && method.length() > 0)
		{
			formPanel.setMethod(method);
		}
		String encoding = element.getAttribute("_encoding");
		if (encoding != null && encoding.length() > 0)
		{
			formPanel.setEncoding(encoding);
		}
		String action = element.getAttribute("_action");
		if (action != null && action.length() > 0)
		{
			formPanel.setAction(action);
		}
	}
	
	@Override
	protected void processEvents(SimplePanel widget, Element element, final String widgetId) throws InterfaceConfigException 
	{
		super.processEvents(widget, element, widgetId);
		FormPanel formPanel = (FormPanel)widget;
		
		final Event eventSubmitComplete = EvtBind.getWidgetEvent(element, Events.EVENT_SUBMIT_COMPLETE);
		if (eventSubmitComplete != null)
		{
			formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler()
			{
				public void onSubmitComplete(SubmitCompleteEvent event) 
				{
					Events.callEvent(eventSubmitComplete, event);
				}
			});
		}
		
		final Event eventSubmit = EvtBind.getWidgetEvent(element, Events.EVENT_SUBMIT);
		if (eventSubmitComplete != null)
		{
			formPanel.addSubmitHandler(new SubmitHandler()
			{
				public void onSubmit(SubmitEvent event) 
				{
					Events.callEvent(eventSubmit, event);
				}
			});
		}
	}
	
	@Override
	protected FormPanel instantiateWidget(Element element, String widgetId) 
	{
		String target = element.getAttribute("_target");
		if (target != null && target.length() >0)
		{
			return new FormPanel(target);
		}
		else
		{
			return new FormPanel();
		}
	}
}
