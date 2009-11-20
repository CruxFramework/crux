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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gessé S. F. Dafé <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="dialogBox", library="gwt")
public class DialogBoxFactory extends PanelFactory<DialogBox>
       implements HasAnimationFactory<DialogBox>, HasCloseHandlersFactory<DialogBox> 
{
	@Override
	public DialogBox instantiateWidget(Element element, String widgetId) 
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

		return new DialogBox(autoHide, modal);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="previewAllNativeEvents", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="autoHide", type=Boolean.class),
		@TagAttributeDeclaration(value="modal", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<DialogBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		DialogBox widget = context.getWidget();

		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}
}
