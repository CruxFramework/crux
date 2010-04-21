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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.widgets.client.event.step.EnterEvent;
import br.com.sysmap.crux.widgets.client.event.step.EnterHandler;
import br.com.sysmap.crux.widgets.client.event.step.HasEnterHandlers;
import br.com.sysmap.crux.widgets.client.event.step.HasLeaveHandlers;
import br.com.sysmap.crux.widgets.client.event.step.LeaveEvent;
import br.com.sysmap.crux.widgets.client.event.step.LeaveHandler;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractWidgetStep extends Composite implements HasEnterHandlers, HasLeaveHandlers, HasCommands
{
	private List<WizardCommand> commands = new ArrayList<WizardCommand>();
	
	/**
	 * @param widget
	 */
	AbstractWidgetStep()
    {
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.step.HasEnterHandlers#addEnterHandler(br.com.sysmap.crux.widgets.client.event.step.EnterHandler)
	 */
	public HandlerRegistration addEnterHandler(EnterHandler handler)
	{
		return addHandler(handler, EnterEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.event.step.HasLeaveHandlers#addLeaveHandler(br.com.sysmap.crux.widgets.client.event.step.LeaveHandler)
	 */
	public HandlerRegistration addLeaveHandler(LeaveHandler handler)
	{
		return addHandler(handler, LeaveEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasCommands#getCommands()
	 */
	public List<WizardCommand> getCommands()
    {
	    return commands;
    }
	
	/**
	 * @param command
	 * @return
	 */
	public boolean addCommand(WizardCommand command)
	{
		return commands.add(command);
	}
	
	/**
	 * @param command
	 * @return
	 */
	public boolean removeCommand(WizardCommand command)
	{
		return commands.remove(command);
	}
}
