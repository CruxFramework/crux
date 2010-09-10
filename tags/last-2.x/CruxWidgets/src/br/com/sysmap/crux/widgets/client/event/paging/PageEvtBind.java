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
package br.com.sysmap.crux.widgets.client.event.paging;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.widgets.client.event.Events;

import com.google.gwt.dom.client.Element;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
 */
public class PageEvtBind extends EvtBind
{
	/**
	 * @param widget
	 */
	public static void bindEvent(Element element, HasPageHandlers widget)
	{
		final Event pageEvent = getWidgetEvent(element, Events.PAGE);
		if (pageEvent != null)
		{
			widget.addPageHandler(new PageHandler()
			{
				public void onPage(PageEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(pageEvent, event);					
				}				
			});
		}
	}
}