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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * CheckBoxFactory DeclarativeFactory.
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="checkBox", library="gwt")
public class CheckBoxFactory extends AbstractCheckBoxFactory<CheckBox>
{
	/**
	 * process widget attributes
	 * @throws InterfaceConfigException 
	 */
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="checked", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<CheckBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		CheckBox widget = context.getWidget();

		String checked = context.readWidgetProperty("checked");
		if (checked != null && checked.trim().length() > 0)
		{
			widget.setValue(Boolean.parseBoolean(checked));
		}

		String innerHtml = element.getInnerHTML();
		String text = context.readWidgetProperty("text");		
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}
	
	@Override
	public CheckBox instantiateWidget(Element element, String widgetId) 
	{
		return new CheckBox();
	}
}
