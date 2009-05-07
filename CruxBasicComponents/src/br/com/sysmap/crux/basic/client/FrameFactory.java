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

import br.com.sysmap.crux.core.client.component.WidgetFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Frame;

/**
 * Factory to create Frame Widgets
 * @author Thiago Bustamante
 */
public class FrameFactory extends WidgetFactory<Frame>
{
	@Override
	protected void processAttributes(Frame widget, Element element, String widgetId)
	{
		super.processAttributes(widget, element, widgetId);

		String url = element.getAttribute("_url");
		if (url != null && url.length() > 0)
		{
			widget.setUrl(url);
		}
	}

	@Override
	protected Frame instantiateWidget(Element element, String widgetId) 
	{
		return new Frame();
	}	
}
