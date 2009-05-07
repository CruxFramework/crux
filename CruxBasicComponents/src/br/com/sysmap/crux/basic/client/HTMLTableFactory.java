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


import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class HTMLTableFactory <T extends HTMLTable> extends PanelFactory<T>
{

	@Override
	protected void processEvents(T widget, Element element, String widgetId)
	{
		super.processEvents(widget, element, widgetId);

		ClickEvtBind.bindEvent(element, widget, widgetId);		
	}
	
	@Override
	protected void processAttributes(T widget, Element element, String widgetId)
	{
		super.processAttributes(widget, element, widgetId);

		String borderWidth = element.getAttribute("_borderWidth");
		if (borderWidth != null && borderWidth.length() > 0)
		{
			widget.setBorderWidth(Integer.parseInt(borderWidth));
		}
	
		String cellPadding = element.getAttribute("_cellPadding");
		if (cellPadding != null && cellPadding.length() > 0)
		{
			widget.setCellPadding(Integer.parseInt(cellPadding));
		}
		
		String cellSpacing = element.getAttribute("_cellSpacing");
		if (cellSpacing != null && cellSpacing.length() > 0)
		{
			widget.setCellSpacing(Integer.parseInt(cellSpacing));
		}
		
	}
	
}
