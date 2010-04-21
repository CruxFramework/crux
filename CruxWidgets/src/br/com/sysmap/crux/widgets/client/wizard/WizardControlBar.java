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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.widgets.client.WidgetMessages;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WizardControlBar extends Composite implements WizardStepListener
{
	public static final String DEFAULT_STYLE_NAME = "crux-WizardControlBar";

	public static String CANCEL_COMMAND = "cancel";
	public static String PREVIOUS_COMMAND = "previous";
	public static String NEXT_COMMAND = "next";
	public static String FINISH_COMMAND = "finish";
	
	private CellPanel cellPanel;
	private Map<String, WizardCommand> commands = new HashMap<String, WizardCommand>();
	private List<String> stepCommands = new ArrayList<String>();
	private Wizard wizard;
	
	private static WidgetMessages messages = GWT.create(WidgetMessages.class);
	
	/**
	 * @param wizard
	 */
	public WizardControlBar()
    {
		this(messages.wizardPreviousCommand(),  messages.wizardNextCommand(), messages.wizardCancelCommand(), messages.wizardFinishCommand(), false);
    }
	
	/**
	 * @param wizard
	 * @param previousLabel
	 * @param nextLabel
	 * @param cancelLabel
	 * @param finishLabel
	 */
	public WizardControlBar(String previousLabel, String nextLabel, String cancelLabel, String finishLabel, boolean vertical)
	{
		
		if (vertical)
		{
			this.cellPanel = new VerticalPanel();
		}
		else
		{
			this.cellPanel = new HorizontalPanel();
		}
		this.cellPanel.setStyleName(DEFAULT_STYLE_NAME);//TODO - Thiago -rever o estilo para vertical / horizontal
		
		commands.put(PREVIOUS_COMMAND, new WizardCommand(this, PREVIOUS_COMMAND, 0, previousLabel, new Command()
		{
			public void execute()
			{
				previous();
			}
		}));
		commands.put(NEXT_COMMAND, new WizardCommand(this, NEXT_COMMAND, 1, nextLabel, new Command()
		{
			public void execute()
			{
				next();
			}
		}));
		commands.put(CANCEL_COMMAND, new WizardCommand(this, CANCEL_COMMAND, 2, cancelLabel, new Command()
		{
			public void execute()
			{
				cancel();
			}
		}));
		commands.put(FINISH_COMMAND, new WizardCommand(this, FINISH_COMMAND, 3, finishLabel, new Command()
		{
			public void execute()
			{
				finish();
			}
		}));
		updateCommands();
		initWidget(cellPanel);
    }
	
	/**
	 * 
	 */
	public void finish()
    {
		checkWizard();
		wizard.finish();
    }

	/**
	 * 
	 */
	public void cancel()
    {
		checkWizard();
		wizard.cancel();
    }

	/**
	 * 
	 */
	public void next()
    {
		checkWizard();
	    wizard.next();
    }

	/**
	 * 
	 */
	public void previous()
    {
		checkWizard();
	    wizard.previous();
    }

	/**
	 * @param id
	 * @param command
	 * @param order
	 */
	public void addCommand(String id, String label, Command command, int order)
	{
		addCommand(id, label, command, order, true);
	}
	
	/**
	 * @param id
	 * @param command
	 * @param order
	 * @param updateBar
	 */
	public void addCommand(String id, String label, Command command, int order, boolean updateBar)
	{
		commands.put(id, new WizardCommand(this, id, order, label, command));
		if (updateBar)
		{
			updateCommands();
		}
	}

	/**
	 * @param id
	 * @return
	 */
	public WizardCommand getCommand(String id)
	{
		return commands.get(id);
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardStepListener#stepChanged(br.com.sysmap.crux.widgets.client.wizard.Step, br.com.sysmap.crux.widgets.client.wizard.Step)
	 */
	public void stepChanged(Step currentStep, Step previousStep)
    {
		checkWizard();
	    int currentOrder = wizard.getStepOrder(currentStep.getId());
		
    	commands.get(PREVIOUS_COMMAND).setEnabled(currentOrder > 0);
    	commands.get(NEXT_COMMAND).setEnabled(currentOrder < (wizard.getStepCount()-1));
    	
    	updateStepCommands(currentStep);
    }
	
	/**
	 * @return
	 */
	public Wizard getWizard()
    {
    	return wizard;
    }

	/**
	 * @param wizard
	 */
	void setWizard(Wizard wizard)
    {
    	this.wizard = wizard;
    }

	/**
	 * @param currentStep
	 */
	private void updateStepCommands(Step currentStep)
    {
		for (String command : stepCommands)
        {
			commands.remove(command);
        }
	    stepCommands.clear();
	    
	    List<WizardCommand> commands = currentStep.getCommands();
	    if (commands != null)
	    {
	    	for (WizardCommand command: commands)
	    	{
	    		String commandId = command.getId();
	    		command.setControlBar(this);
	    		this.commands.put(commandId, command);
	    		stepCommands.add(commandId);
	    	}
		}
	    updateCommands();
    }

	/**
	 * 
	 */
	private void checkWizard()
	{
		if (this.wizard == null)
		{
			throw new NullPointerException(messages.wizardControlBarOrphan());
		}
	}
	
	/**
	 * 
	 */
	private void updateCommands()
    {
		List<WizardCommand> sortedCommands = new ArrayList<WizardCommand>();

		for (WizardCommand command: commands.values())
		{
			sortedCommands.add(command);
		}

		Collections.sort(sortedCommands);
		
		cellPanel.clear();
		
		for (final WizardCommand command: sortedCommands)
		{
			cellPanel.add(command.button);
		}
    }

	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static class WizardCommand implements Comparable<WizardCommand>
	{
		private WizardControlBar controlBar;
		private String id;
		private int order;
		private DecoratedButton button;
		
		WizardCommand(WizardControlBar controlBar, String id, int order, String label, Command command)
		{
			this(id, order, label, command);
			setControlBar(controlBar);
		}
		
		public WizardCommand(String id, int order, String label, Command command)
		{
			//TODO - Thiago - ver estilo para os botoes de comando
			this.id = id;
			this.order = order;
			this.button = new DecoratedButton();
			setLabel(label);
			setCommand(command);
		}
		
		public int getOrder()
        {
        	return order;
        }

		public void setOrder(int order)
        {
        	this.order = order;
        	if (controlBar != null)
        	{
        		controlBar.updateCommands();
        	}
        }

		public String getLabel()
        {
        	return button.getText();
        }

		public void setLabel(String label)
        {
        	this.button.setText(label);
        }

		public boolean isVisible()
        {
        	return this.button.isVisible();
        }

		public void setVisible(boolean visible)
        {
        	this.button.setVisible(visible);
        }

		public boolean isEnabled()
        {
        	return this.button.isEnabled();
        }

		public void setEnabled(boolean enabled)
        {
        	this.button.setEnabled(enabled);
        }

		public String getId()
        {
        	return id;
        }

		public int compareTo(WizardCommand o)
        {
			if (o == null)
			{
				return 1;
			}
	        return order==o.order?0:order<o.order?-1:1;
        }

		void setControlBar(WizardControlBar controlBar)
		{
			this.controlBar = controlBar;
		}
		
		private void setCommand(final Command command)
        {
			if (command != null)
			{
				button.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						command.execute();
					}
				});
			}
        }
	}
}
