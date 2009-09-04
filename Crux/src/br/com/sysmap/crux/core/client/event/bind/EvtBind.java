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

/**
 * Helper Class for events binding
 * @author Thiago Bustamante
 *
 */
public class EvtBind 
{ 
	/**
	 * Builds an TagEvent object from the page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @param element
	 * @param evtId
	 * @return
	 */
	public static Event getWidgetEvent(Element element, String evtId)
	{
		String evt = element.getAttribute("_"+evtId);
		return Events.getEvent(evtId, evt);
	}
}
