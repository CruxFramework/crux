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
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Decorated Button widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="dynaTabs", library="adv")
public class DynaTabsFactory extends WidgetFactory<DynaTabs>
{
	@Override
	public DynaTabs instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new DynaTabs();
	}

	@Override
	public void processChildren(WidgetFactoryContext<DynaTabs> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		DynaTabs widget = context.getWidget();
		
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
			new BeforeCloseEvtBind().bindEvent(child, tab);
		}
	}
}