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
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.LoadErrorEvtBind;
import br.com.sysmap.crux.core.client.event.bind.LoadEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;

/**
 * Represents an ImageFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="image", library="gwt")
public class ImageFactory extends WidgetFactory<Image> 
	   implements HasClickHandlersFactory<Image>, HasAllMouseHandlersFactory<Image>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="url", required=true)
	})	
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("leftRect"),
		@TagAttributeDeclaration("topRect"),
		@TagAttributeDeclaration("widthRect"),
		@TagAttributeDeclaration("heightRect")
	})	
	public void processAttributes(WidgetFactoryContext<Image> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		Image widget = context.getWidget();

		String leftStr = element.getAttribute("_leftRect");
		String topStr = element.getAttribute("_topRect");
		String widthStr = element.getAttribute("_widthRect");
		String heightStr = element.getAttribute("_heightRect");
		if (leftStr != null && topStr != null && widthStr != null && heightStr != null
			&& leftStr.length() > 0 && topStr.length() > 0 && widthStr.length() > 0 && heightStr.length() > 0)
		{
			widget.setVisibleRect(Integer.parseInt(leftStr),Integer.parseInt(topStr), 
					Integer.parseInt(widthStr), Integer.parseInt(heightStr));
		}
	}
	
	@Override
	@TagEvents({
		@TagEvent(LoadEvtBind.class),
		@TagEvent(LoadErrorEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<Image> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	public Image instantiateWidget(Element element, String widgetId) 
	{
		return new Image();
	}
}
