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
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;


/**
 * Represents a PopupPanelFactory
 * @author Thiago Bustamante
 *
 */
public class PopupPanelFactory extends SimplePanelFactory
{

	@Override
	protected PopupPanel instantiateWidget(Element element, String widgetId) 
	{
		String autoHideStr = element.getAttribute("_autoHide");
		boolean autoHide = false;
		if (autoHideStr != null && autoHideStr.length() >0)
		{
			autoHide = Boolean.parseBoolean(autoHideStr);
		}
		String modalStr = element.getAttribute("_modal");
		boolean modal = false;
		if (modalStr != null && modalStr.length() >0)
		{
			modal = Boolean.parseBoolean(modalStr);
		}

		return new PopupPanel(autoHide, modal);
	}
	
	@Override
	protected void processAttributes(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		PopupPanel popupPanel = (PopupPanel)widget;
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			popupPanel.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		String previewAllNativeEvents = element.getAttribute("_previewAllNativeEvents");
		if (previewAllNativeEvents != null && previewAllNativeEvents.length() > 0)
		{
			popupPanel.setPreviewingAllNativeEvents(Boolean.parseBoolean(previewAllNativeEvents));
		}
		
	}
	
	
	@Override
	protected void processEvents(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		PopupPanel popupPanel = (PopupPanel)widget;
		CloseEvtBind.bindEvent(element, popupPanel, widgetId);
	}
}
