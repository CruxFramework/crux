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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WidgetStep<T extends Serializable> extends AbstractWidgetStep<T> implements HasCommands<T>
{
	protected Map<String, WizardCommand<T>> commands = new LinkedHashMap<String, WizardCommand<T>>();
	protected Wizard<T> wizard;
	private SimplePanel panel;
	
	/**
	 * @param widget
	 */
	WidgetStep(Widget widget, Wizard<T> wizard)
    {
		this.wizard = wizard;
		panel = new SimplePanel();
		panel.setHeight("100%");
		panel.setWidth("100%");
		
		panel.add(widget);
		initWidget(widget);
    }

	@Override
    public boolean addCommand(String id, String label, WizardCommandHandler<T> handler, int order)
    {
		if (!commands.containsKey(id))
		{
			WizardCommand<T> command = new WizardCommand<T>(id, order, label, handler, new WidgetWizardProxy<T>(wizard));
			commands.put(id, command);
			return true;
		}
		return false;
    }
	
    /**
	 * @param id
	 * @return
	 */
	public WizardCommand<T> getCommand(String id)
	{
	    return commands.get(id);
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasCommands#getCommands()
	 */
	public Iterator<WizardCommand<T>> iterateCommands()
    {
	    return commands.values().iterator();
    }
	
	/**
	 * @param command
	 * @return
	 */
	@Override
	public boolean removeCommand(String id)
	{
		if (commands.containsKey(id))
		{
			commands.remove(id);
			return true;
		}
		return false;
	}
	
	boolean addCommand(String id, String label, final Event commandEvent, int order)
    {
		if (!commands.containsKey(id))
		{
			WizardCommandHandler<T> handler = new WizardCommandHandler<T>()
			{
				public void onCommand(WizardCommandEvent<T> event)
				{
					Events.callEvent(commandEvent, event);
				}
			};
			WizardCommand<T> command = new WizardCommand<T>(id, order, label, handler, new WidgetWizardProxy<T>(wizard));
			commands.put(id, command);
			return true;
		}
		return false;
    }
}
