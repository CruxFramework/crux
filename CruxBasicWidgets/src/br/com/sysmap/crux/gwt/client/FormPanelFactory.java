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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

/**
 * Represents a FormPanelFactory.
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="formPanel", library="gwt")
public class FormPanelFactory extends PanelFactory<FormPanel>
{
	
	@Override
	@TagAttributes({
		@TagAttribute("method"),
		@TagAttribute("encoding"),
		@TagAttribute("action")
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("target")
	})
    public void processAttributes(WidgetFactoryContext<FormPanel> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onSubmitComplete"),
		@TagEventDeclaration("onSubmit")
	})
	public void processEvents(WidgetFactoryContext<FormPanel> context) throws InterfaceConfigException 
	{
		super.processEvents(context);
		
		Element element = context.getElement();
		FormPanel widget = context.getWidget();
		
		final Event eventSubmitComplete = EvtBind.getWidgetEvent(element, Events.EVENT_SUBMIT_COMPLETE);
		if (eventSubmitComplete != null)
		{
			widget.addSubmitCompleteHandler(new SubmitCompleteHandler()
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
			widget.addSubmitHandler(new SubmitHandler()
			{
				public void onSubmit(SubmitEvent event) 
				{
					Events.callEvent(eventSubmit, event);
				}
			});
		}
	}
	
	@Override
	public FormPanel instantiateWidget(Element element, String widgetId) 
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
