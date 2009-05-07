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
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.LoadEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;

/**
 * Represents an ImageFactory component
 * @author Thiago Bustamante
 */
public class ImageFactory extends WidgetFactory<Image>
{
	@Override
	protected void processAttributes(Image widget, Element element, String widgetId)
	{
		super.processAttributes(widget, element, widgetId);

		String url = element.getAttribute("_url");
		if (url != null && url.length() > 0)
		{
			widget.setUrl(url);
		}

		String leftStr = element.getAttribute("_leftRect");
		String topStr = element.getAttribute("_topRect");
		String widthStr = element.getAttribute("_widthRect");
		String heightStr = element.getAttribute("_heightRect");
		if (leftStr != null && topStr != null && widthStr != null && heightStr != null)
		{
			widget.setVisibleRect(Integer.parseInt(leftStr),Integer.parseInt(topStr), 
					Integer.parseInt(widthStr), Integer.parseInt(heightStr));
		}
	}
	
	@Override
	protected void processEvents(Image widget, Element element, String widgetId)
	{
		super.processEvents(widget, element, widgetId);

		ClickEvtBind.bindEvent(element, widget, widgetId);
		MouseEvtBind.bindEvents(element, widget, widgetId);
		LoadEvtBind.bindLoadEvent(element, widget, widgetId);
		LoadEvtBind.bindErrorEvent(element, widget, widgetId);
	}

	@Override
	protected Image instantiateWidget(Element element, String widgetId) 
	{
		return new Image();
	}
}
