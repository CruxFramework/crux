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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractWidgetStep extends Composite implements HasEnterHandlers, HasLeaveHandlers
{
	/**
	 * @param widget
	 */
	AbstractWidgetStep()
    {
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasEnterHandlers#addEnterHandler(br.com.sysmap.crux.widgets.client.wizard.EnterHandler)
	 */
	public HandlerRegistration addEnterHandler(EnterHandler handler)
	{
		return addHandler(handler, EnterEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasLeaveHandlers#addLeaveHandler(br.com.sysmap.crux.widgets.client.wizard.LeaveHandler)
	 */
	public HandlerRegistration addLeaveHandler(LeaveHandler handler)
	{
		return addHandler(handler, LeaveEvent.getType());
	}

	/**
	 * @param command
	 * @return
	 */
	public abstract boolean addCommand(String id, String label, WizardCommandHandler handler, int order);
	
	/**
	 * @param command
	 * @return
	 */
	public abstract boolean removeCommand(String id);
}
