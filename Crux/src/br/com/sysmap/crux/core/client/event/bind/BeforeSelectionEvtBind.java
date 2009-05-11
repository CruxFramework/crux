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
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;

/**
 * Helper Class for beforeSelection events binding
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class BeforeSelectionEvtBind extends EvtBind
{
	public static <I> void bindEvent(Element element, HasBeforeSelectionHandlers<I> widget, final String widgetId)
	{
		final Event eventBeforeSelection = getWidgetEvent(element, EventFactory.EVENT_BEFORE_SELECTION);
		if (eventBeforeSelection != null)
		{
			widget.addBeforeSelectionHandler(new BeforeSelectionHandler<I>()
			{
				public void onBeforeSelection(BeforeSelectionEvent<I> event)
				{
					EventFactory.callEvent(eventBeforeSelection, widgetId);
				}
			});
		}
	}

}
