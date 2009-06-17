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
package br.com.sysmap.crux.core.client.event.bind;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * Helper Class for selection events binding
 * @author Thiago Bustamante
 *
 */
public class SelectionEvtBind extends EvtBind
{
	public static <I> void bindEvent(Element element, HasSelectionHandlers<I> widget)
	{
		final Event eventChange = getWidgetEvent(element, Events.EVENT_SELECTION);
		if (eventChange != null)
		{
			widget.addSelectionHandler(new SelectionHandler<I>()
			{
				public void onSelection(SelectionEvent<I> event) 
				{
					Events.callEvent(eventChange, event);
				}
			});
		}
	}
}
