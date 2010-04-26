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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetStep extends AbstractWidgetStep implements HasCommands
{
	private SimplePanel panel;
	protected Wizard wizard;
	protected Map<String, WizardCommand> commands = new LinkedHashMap<String, WizardCommand>();
	
	/**
	 * @param widget
	 */
	WidgetStep(Widget widget, Wizard wizard)
    {
		this.wizard = wizard;
		panel = new SimplePanel();
		panel.setHeight("100%");
		panel.setWidth("100%");
		
		panel.add(widget);
		initWidget(widget);
    }

	@Override
    public boolean addCommand(String id, String label, WizardCommandHandler handler, int order)
    {
		if (!commands.containsKey(id))
		{
			WizardCommand command = new WizardCommand(id, order, label, handler, new WidgetWizardProxy(wizard));
			commands.put(id, command);
			return true;
		}
		return false;
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.HasCommands#getCommands()
	 */
	public Iterator<WizardCommand> iterateCommands()
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
}
