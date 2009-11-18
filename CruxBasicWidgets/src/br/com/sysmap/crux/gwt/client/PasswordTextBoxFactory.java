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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * Represents a PasswordTextBoxFactory component
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="passwordTextBox", library="gwt")
public class PasswordTextBoxFactory extends TextBoxBaseFactory<PasswordTextBox> 
       implements HasDirectionFactory<PasswordTextBox>  
{
	@Override
	@TagAttributes({
		@TagAttribute(value="maxLength", type=Integer.class),
		@TagAttribute(value="visibleLength", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<PasswordTextBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	public PasswordTextBox instantiateWidget(Element element, String widgetId) 
	{
		return new PasswordTextBox();
	}
}
