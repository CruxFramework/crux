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
package br.com.sysmap.crux.widgets.client.wizard;

import java.io.Serializable;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public abstract class AbstractWidgetStep<T extends Serializable> extends Composite implements HasEnterHandlers<T>, HasLeaveHandlers<T>
{
	/**
	 * @param widget
	 */
	AbstractWidgetStep()
    {
    }
	
	/**
	 * @param command
	 * @return
	 */
	public abstract boolean addCommand(String id, String label, WizardCommandHandler<T> handler, int order);

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasEnterHandlers#addEnterHandler(br.com.sysmap.crux.widgets.client.wizard.EnterHandler)
	 */
	public HandlerRegistration addEnterHandler(EnterHandler<T> handler)
	{
		return addHandler(handler, EnterEvent.getType(handler));
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasLeaveHandlers#addLeaveHandler(br.com.sysmap.crux.widgets.client.wizard.LeaveHandler)
	 */
	public HandlerRegistration addLeaveHandler(LeaveHandler<T> handler)
	{
		return addHandler(handler, LeaveEvent.getType(handler));
	}

	/**
	 * @param command
	 * @return
	 */
	public abstract boolean removeCommand(String id);
	
	/**
	 * @param onEnterEvent
	 */
	public void addEnterEvent(final Event onEnterEvent)
	{
		addEnterHandler(new EnterHandler<T>(){
			public void onEnter(EnterEvent<T> event)
			{
				Events.callEvent(onEnterEvent, event);
			}
		});
	}
	
	public void addLeaveEvent(final Event onLeaveEvent)
	{
		addLeaveHandler(new LeaveHandler<T>()
		{
			public void onLeave(LeaveEvent<T> event)
			{
				Events.callEvent(onLeaveEvent, event);
			}
		});
	}
}
