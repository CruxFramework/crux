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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;

/**
 * Represents a DecoratedPopupPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="decoratedPopupPanel", library="bas")
public class DecoratedPopupPanelFactory extends PopupPanelFactory 
{
	@Override
	protected DecoratedPopupPanel instantiateWidget(Element element, String widgetId) 
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

		return new DecoratedPopupPanel(autoHide, modal);
	}
}
