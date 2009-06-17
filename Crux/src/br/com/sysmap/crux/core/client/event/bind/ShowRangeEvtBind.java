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
import com.google.gwt.event.logical.shared.HasShowRangeHandlers;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;

/**
 * Helper Class for showRange events binding
 * @author Thiago Bustamante
 *
 */
public class ShowRangeEvtBind extends EvtBind
{
	public static <V> void bindEvent(Element element, HasShowRangeHandlers<V> widget)
	{
		final Event eventShowRange = EvtBind.getWidgetEvent(element, "_onshowrange");
		if (eventShowRange != null)
		{
			widget.addShowRangeHandler(new ShowRangeHandler<V>()
			{
				public void onShowRange(ShowRangeEvent<V> event)
				{
					Events.callEvent(eventShowRange, event);
				}
			});
		}		
	}

}
