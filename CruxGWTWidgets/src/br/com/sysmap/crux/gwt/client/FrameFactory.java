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
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Frame;

/**
 * Factory to create Frame Widgets
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="frame", library="gwt")
public class FrameFactory extends WidgetFactory<Frame>
{
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("url")
	})
	public void processAttributes(WidgetFactoryContext<Frame> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		String url = context.readWidgetProperty("url");
		if (!StringUtils.isEmpty(url))
		{
			context.getWidget().setUrl(Screen.appendDebugParameters(url));
		}
	}

	@Override
	public Frame instantiateWidget(Element element, String widgetId) 
	{
		return new Frame();
	}	
}
