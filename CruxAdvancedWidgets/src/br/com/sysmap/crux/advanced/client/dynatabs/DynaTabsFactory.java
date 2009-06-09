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
package br.com.sysmap.crux.advanced.client.dynatabs;

import java.util.List;

import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusOrBlurEvtBind;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeOpenOrCloseEvtBind;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Decorated Button widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DynaTabsFactory extends WidgetFactory<DynaTabs>
{
	@Override
	protected DynaTabs instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new DynaTabs();
	}
	
	@Override
	protected void processAttributes(DynaTabs widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		List<Element> tabs = ensureChildrenSpans(element, true);
		for (Element child : tabs)
		{
			String id = child.getAttribute("_id");
			String label = child.getAttribute("_label");
			String url = child.getAttribute("_url");
						
			boolean closeable = true;
			String strCloseable = child.getAttribute("_closeable");
			if(strCloseable != null && strCloseable.trim().length() > 0)
			{
				closeable = Boolean.parseBoolean(strCloseable);
			}
			
			Tab tab = widget.openTab(id, label, url, closeable, false);
			
			BeforeFocusOrBlurEvtBind.bindEvents(child, tab);
			BeforeOpenOrCloseEvtBind.bindBeforeCloseEvent(child, tab);
		}
	}
}