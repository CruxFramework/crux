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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasCloseHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;

/**
 * Represents a DecoratedPopupPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="decoratedPopupPanel", library="gwt")
public class DecoratedPopupPanelFactory extends PanelFactory<DecoratedPopupPanel>
             implements HasAnimationFactory<DecoratedPopupPanel>, HasCloseHandlersFactory<DecoratedPopupPanel>
{
	@Override
	public DecoratedPopupPanel instantiateWidget(Element element, String widgetId) 
	{
		String autoHideStr = getProperty(element,"autoHide");
		boolean autoHide = false;
		if (autoHideStr != null && autoHideStr.length() >0)
		{
			autoHide = Boolean.parseBoolean(autoHideStr);
		}
		String modalStr = getProperty(element,"modal");
		boolean modal = false;
		if (modalStr != null && modalStr.length() >0)
		{
			modal = Boolean.parseBoolean(modalStr);
		}

		return new DecoratedPopupPanel(autoHide, modal);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="previewingAllNativeEvents", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="autoHide", type=Boolean.class),
		@TagAttributeDeclaration(value="modal", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<DecoratedPopupPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
}
