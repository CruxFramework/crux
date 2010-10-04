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
package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HasWidgetsHandler
{
	/**
	 * @param widget
	 * @param widgetId
	 */
	public static void handleWidgetElement(Widget widget, String widgetId, String factoryType)
	{
		widget.getElement().setAttribute("_hasWidgetsPanel", widgetId);
		widget.getElement().setAttribute("_type", factoryType);
	}
	
	/**
	 * @param parentElement
	 * @return
	 */
	public static String getHasWidgetsId(Element parentElement)
	{
		return parentElement.getAttribute("_hasWidgetsPanel");		
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public static boolean isValidHasWidgetsPanel(Element element)
	{
		String type = element.getAttribute("_hasWidgetsPanel");
		if (type != null && type.length() > 0)
		{
			return true;
		}
		return false;
	}	
}
