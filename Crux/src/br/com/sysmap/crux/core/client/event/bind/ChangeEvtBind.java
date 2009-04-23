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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Helper Class for change events binding
 * @author Thiago Bustamante
 *
 */
public class ChangeEvtBind extends EvtBind
{
	public static <I> void bindValueEvent(Element element, HasValueChangeHandlers<I> widget, final String componentId)
	{
		final Event eventChange = getComponentEvent(element, EventFactory.EVENT_CHANGE);
		if (eventChange != null)
		{
			widget.addValueChangeHandler(new ValueChangeHandler<I>()
			{
				@Override
				public void onValueChange(ValueChangeEvent<I> event) 
				{
					EventFactory.callEvent(eventChange, componentId);
				}
			});
		}
	}

	public static <I> void bindEvent(Element element, HasChangeHandlers widget, final String componentId)
	{
		final Event eventChange = getComponentEvent(element, EventFactory.EVENT_CHANGE);
		if (eventChange != null)
		{
			widget.addChangeHandler(new ChangeHandler()
			{
				@Override
				public void onChange(ChangeEvent event) 
				{
					EventFactory.callEvent(eventChange, componentId);
				}
			});
		}
	}
}
