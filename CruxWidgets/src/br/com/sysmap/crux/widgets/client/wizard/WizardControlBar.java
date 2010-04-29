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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
	private boolean vertical;
	private String buttonWidth;
	private String buttonHeight;
	private String buttonStyle;
	private String previousLabel;
	private String nextLabel;
	private String cancelLabel;
	private String finishLabel;
	private int previousOrder = 0;
	private int nextOrder = 1;
	private int cancelOrder = 2;
	private int finishOrder = 3;
	
	/**
	 * @param wizard
	 */
	public WizardControlBar()
    {
		this(false);
    }
	
	/**
	 * @param wizard
	 * @param previousLabel
	 * @param nextLabel
	 * @param cancelLabel
	 * @param finishLabel
	 */
	public WizardControlBar(boolean vertical)
	{
		this.previousLabel = WidgetMsgFactory.getMessages().wizardPreviousCommand();
		this.nextLabel = WidgetMsgFactory.getMessages().wizardNextCommand();
		this.cancelLabel = WidgetMsgFactory.getMessages().wizardCancelCommand();
		this.finishLabel = WidgetMsgFactory.getMessages().wizardFinishCommand();
		this.vertical = vertical;
		
		if (vertical)
		{
			this.cellPanel = new VerticalPanel();
		}
		else
		{
			this.cellPanel = new HorizontalPanel();
		}
		this.cellPanel.setStyleName(DEFAULT_STYLE_NAME);
		
		initWidget(cellPanel);
		setSpacing(5);
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
	 * @param handler
	 * @param order
	 */
	public void addCommand(String id, String label, WizardCommandHandler handler, int order)
	{
		addCommand(id, label, handler, order, true);
	}
	
	/**
	 * @param id
	 * @param handler
	 * @param order
	 * @param updateBar
	 */
	public void addCommand(String id, String label, WizardCommandHandler handler, int order, boolean updateBar)
	{
		checkWizard();
		commands.put(id, new WizardCommand(this, id, order, label, handler, new WidgetWizardProxy(wizard)));
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
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		cellPanel.setSpacing(spacing);
	}
	
	/**
	 * @return
	 */
	public int getSpacing()
	{
		return cellPanel.getSpacing();
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
	 * @return
	 */
	public boolean isVertical()
	{
		return vertical;
	}
	
	public String getButtonsWidth()
    {
    	return buttonWidth;
    }

	public void setButtonsWidth(String buttonWidth)
    {
    	this.buttonWidth = buttonWidth;
    	updateCommandButtons();
    }

	public String getButtonsHeight()
    {
    	return buttonHeight;
    }

	public void setButtonsHeight(String buttonHeight)
    {
    	this.buttonHeight = buttonHeight;
    	updateCommandButtons();
    }

	public String getButtonsStyle()
    {
    	return buttonStyle;
    }

	public void setButtonsStyle(String buttonStyle)
    {
    	this.buttonStyle = buttonStyle;
    	updateCommandButtons();
    }

	public String getPreviousLabel()
    {
    	return previousLabel;
    }

	public String getNextLabel()
    {
    	return nextLabel;
    }

	public String getCancelLabel()
    {
    	return cancelLabel;
    }

	public String getFinishLabel()
    {
    	return finishLabel;
    }
	
	public int getPreviousOrder()
    {
    	return previousOrder;
    }

	public int getNextOrder()
    {
    	return nextOrder;
    }

	public int getCancelOrder()
    {
    	return cancelOrder;
    }

	public int getFinishOrder()
    {
    	return finishOrder;
    }

	/**
	 * @param previousOrder
	 */
	public void setPreviousOrder(int previousOrder)
    {
    	this.previousOrder = previousOrder;
		WizardCommand command = getCommand(PREVIOUS_COMMAND);
		if (command != null)
		{
			command.setOrder(this.previousOrder);
		}
    }

	/**
	 * @param nextOrder
	 */
	public void setNextOrder(int nextOrder)
    {
    	this.nextOrder = nextOrder;
		WizardCommand command = getCommand(NEXT_COMMAND);
		if (command != null)
		{
			command.setOrder(this.nextOrder);
		}
    }

	/**
	 * @param cancelOrder
	 */
	public void setCancelOrder(int cancelOrder)
    {
    	this.cancelOrder = cancelOrder;
		WizardCommand command = getCommand(CANCEL_COMMAND);
		if (command != null)
		{
			command.setOrder(this.cancelOrder);
		}
    }

	/**
	 * @param finishOrder
	 */
	public void setFinishOrder(int finishOrder)
    {
    	this.finishOrder = finishOrder;
		WizardCommand command = getCommand(FINISH_COMMAND);
		if (command != null)
		{
			command.setOrder(this.finishOrder);
		}
    }

	/**
	 * @param previousLabel
	 */
	public void setPreviousLabel(String previousLabel)
	{
		this.previousLabel = previousLabel;
		WizardCommand command = getCommand(PREVIOUS_COMMAND);
		if (command != null)
		{
			command.setLabel(this.previousLabel);
		}
	}
	
	/**
	 * @param nextLabel
	 */
	public void setNextLabel(String nextLabel)
	{
		this.nextLabel = nextLabel;
		WizardCommand command = getCommand(NEXT_COMMAND);
		if (command != null)
		{
			command.setLabel(this.nextLabel);
		}
	}
	
	/**
	 * @param cancelLabel
	 */
	public void setCancelLabel(String cancelLabel)
	{
		this.cancelLabel = cancelLabel;
		WizardCommand command = getCommand(CANCEL_COMMAND);
		if (command != null)
		{
			command.setLabel(this.cancelLabel);
		}
	}
	
	/**
	 * @param finishLabel
	 */
	public void setFinishLabel(String finishLabel)
	{
		this.finishLabel = finishLabel;
		WizardCommand command = getCommand(FINISH_COMMAND);
		if (command != null)
		{
			command.setLabel(this.finishLabel);
		}
	}

	/**
	 * @param wizard
	 */
	void setWizard(Wizard wizard)
    {
    	this.wizard = wizard;
    	addDefaultCommands();
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
	    
	    Iterator<WizardCommand> commands = currentStep.iterateCommands();
	    if (commands != null)
	    {
	    	while (commands.hasNext())
	    	{
	    		WizardCommand command = commands.next();
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
			throw new NullPointerException(WidgetMsgFactory.getMessages().wizardControlBarOrphan());
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
			cellPanel.add(command);
		}
    }

	/**
	 * 
	 */
	private void updateCommandButtons()
    {
		for (WizardCommand command: commands.values())
		{
			command.setControlBar(this);
		}
    }

	/**
	 * @param previousLabel
	 * @param nextLabel
	 * @param cancelLabel
	 * @param finishLabel
	 */
	private void addDefaultCommands()
    {
		checkWizard();
		commands.put(PREVIOUS_COMMAND, new WizardCommand(this, PREVIOUS_COMMAND, this.previousOrder, previousLabel, new WizardCommandHandler()
		{
			
			public void onCommand(WizardCommandEvent event)
			{
				previous();
			}
		}, new WidgetWizardProxy(wizard)));
		commands.put(NEXT_COMMAND, new WizardCommand(this, NEXT_COMMAND, this.nextOrder, nextLabel, new WizardCommandHandler()
		{
			
			public void onCommand(WizardCommandEvent event)
			{
				next();
			}
		}, new WidgetWizardProxy(wizard)));
		commands.put(CANCEL_COMMAND, new WizardCommand(this, CANCEL_COMMAND, this.cancelOrder, cancelLabel, new WizardCommandHandler()
		{
			
			public void onCommand(WizardCommandEvent event)
			{
				cancel();
			}
		}, new WidgetWizardProxy(wizard)));
		commands.put(FINISH_COMMAND, new WizardCommand(this, FINISH_COMMAND, this.finishOrder, finishLabel, new WizardCommandHandler()
		{
			
			public void onCommand(WizardCommandEvent event)
			{
				finish();
			}
		}, new WidgetWizardProxy(wizard)));
		updateCommands();
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static class WizardCommand extends Composite implements Comparable<WizardCommand>, HasWizardCommandHandlers
	{
		private WizardControlBar controlBar;
		private String id;
		private int order;
		private DecoratedButton button;
		private WizardProxy proxy;
		
		WizardCommand(WizardControlBar controlBar, String id, int order, String label, WizardCommandHandler commandHandler, WizardProxy proxy)
		{
			this(id, order, label, commandHandler, proxy);
			setControlBar(controlBar);
		}
		
		WizardCommand(String id, int order, String label, WizardCommandHandler commandHandler, WizardProxy proxy)
		{
			this.id = id;
			this.order = order;
			this.proxy = proxy;
			button = new DecoratedButton();
			button.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					WizardCommandEvent.fire(WizardCommand.this, WizardCommand.this.proxy);
				}
			});
			initWidget(button);
			
			setLabel(label);
			addWizardCommandHandler(commandHandler);
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

		public HandlerRegistration addWizardCommandHandler(WizardCommandHandler handler)
		{
			return addHandler(handler, WizardCommandEvent.getType());
		}
		
		void setControlBar(WizardControlBar controlBar)
		{
			this.controlBar = controlBar;
			if (!StringUtils.isEmpty(this.controlBar.buttonHeight))
			{
				setHeight(this.controlBar.buttonHeight);;
			}
			if (!StringUtils.isEmpty(this.controlBar.buttonWidth))
			{
				setWidth(this.controlBar.buttonWidth);;
			}
			if (StringUtils.isEmpty(getStyleName()) && !StringUtils.isEmpty(this.controlBar.buttonStyle))
			{
				setStyleName(this.controlBar.buttonStyle);;
			}
		}
	}
}
