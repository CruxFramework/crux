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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardControlBar<T extends Serializable> extends AbstractWizardNavigationBar<T>
{
	public static String BACK_COMMAND = "back";

	public static String CANCEL_COMMAND = "cancel";
	public static final String DEFAULT_STYLE_NAME = "crux-WizardControlBar";
	public static String FINISH_COMMAND = "finish";
	public static String NEXT_COMMAND = "next";
	
	private String backLabel;
	private int backOrder = 0;
	private String buttonHeight;
	private String buttonStyle;
	private String buttonWidth;
	private String cancelLabel;
	private int cancelOrder = 3;
	private Map<String, WizardCommand<T>> commands = new HashMap<String, WizardCommand<T>>();
	private String finishLabel;
	private int finishOrder = 2;
	private String nextLabel;
	private int nextOrder = 1;
	private List<String> stepCommands = new ArrayList<String>();
	
	
	/**
	 * @param wizard
	 */
	public WizardControlBar()
    {
		this(false);
    }
	
	/**
	 * @param vertical
	 */
	public WizardControlBar(boolean vertical)
	{
		super(vertical, DEFAULT_STYLE_NAME);
		
		this.backLabel = WidgetMsgFactory.getMessages().wizardBackCommand();
		this.nextLabel = WidgetMsgFactory.getMessages().wizardNextCommand();
		this.cancelLabel = WidgetMsgFactory.getMessages().wizardCancelCommand();
		this.finishLabel = WidgetMsgFactory.getMessages().wizardFinishCommand();
    }
	
	/**
	 * @param id
	 * @param handler
	 * @param order
	 */
	public void addCommand(String id, String label, WizardCommandHandler<T> handler, int order)
	{
		addCommand(id, label, handler, order, true);
	}
	
	/**
	 * @param id
	 * @param label
	 * @param commandEvent
	 * @param order
	 */
	void addCommand(String id, String label, final Event commandEvent, int order)
	{
		WizardCommandHandler<T> handler = new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				Events.callEvent(commandEvent, event);
			}
		};
		addCommand(id, label, handler, order, true);
	}

	/**
	 * @param id
	 * @param handler
	 * @param order
	 * @param updateBar
	 */
	public void addCommand(String id, String label, WizardCommandHandler<T> handler, int order, boolean updateBar)
	{
		checkWizard();
		commands.put(id, new WizardCommand<T>(this, id, order, label, handler, new WidgetWizardProxy<T>(wizard)));
		if (updateBar)
		{
			updateCommands();
		}
	}

	/**
	 * 
	 */
	public void back()
    {
		checkWizard();
	    wizard.back();
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
	public void finish()
    {
		checkWizard();
		wizard.finish();
    }
	
	public String getBackLabel()
    {
    	return backLabel;
    }

	public int getBackOrder()
    {
    	return backOrder;
    }
	
	public String getButtonsHeight()
    {
    	return buttonHeight;
    }
	
	public String getButtonsStyle()
    {
    	return buttonStyle;
    }

	public String getButtonsWidth()
    {
    	return buttonWidth;
    }

	public String getCancelLabel()
    {
    	return cancelLabel;
    }

	public int getCancelOrder()
    {
    	return cancelOrder;
    }

	/**
	 * @param id
	 * @return
	 */
	public WizardCommand<T> getCommand(String id)
	{
		return commands.get(id);
	}

	public String getFinishLabel()
    {
    	return finishLabel;
    }

	public int getFinishOrder()
    {
    	return finishOrder;
    }

	public String getNextLabel()
    {
    	return nextLabel;
    }

	public int getNextOrder()
    {
    	return nextOrder;
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
	 * @param backLabel
	 */
	public void setBackLabel(String backLabel)
	{
		this.backLabel = backLabel;
		WizardCommand<T> command = getCommand(BACK_COMMAND);
		if (command != null)
		{
			command.setLabel(this.backLabel);
		}
	}

	/**
	 * @param backOrder
	 */
	public void setBackOrder(int backOrder)
    {
    	this.backOrder = backOrder;
		WizardCommand<T> command = getCommand(BACK_COMMAND);
		if (command != null)
		{
			command.setOrder(this.backOrder);
		}
    }

	public void setButtonsHeight(String buttonHeight)
    {
    	this.buttonHeight = buttonHeight;
    	updateCommandButtons();
    }

	public void setButtonsStyle(String buttonStyle)
    {
    	this.buttonStyle = buttonStyle;
    	updateCommandButtons();
    }

	public void setButtonsWidth(String buttonWidth)
    {
    	this.buttonWidth = buttonWidth;
    	updateCommandButtons();
    }

	/**
	 * @param cancelLabel
	 */
	public void setCancelLabel(String cancelLabel)
	{
		this.cancelLabel = cancelLabel;
		WizardCommand<T> command = getCommand(CANCEL_COMMAND);
		if (command != null)
		{
			command.setLabel(this.cancelLabel);
		}
	}

	/**
	 * @param cancelOrder
	 */
	public void setCancelOrder(int cancelOrder)
    {
    	this.cancelOrder = cancelOrder;
		WizardCommand<T> command = getCommand(CANCEL_COMMAND);
		if (command != null)
		{
			command.setOrder(this.cancelOrder);
		}
    }

	/**
	 * @param finishLabel
	 */
	public void setFinishLabel(String finishLabel)
	{
		this.finishLabel = finishLabel;
		WizardCommand<T> command = getCommand(FINISH_COMMAND);
		if (command != null)
		{
			command.setLabel(this.finishLabel);
		}
	}

	/**
	 * @param finishOrder
	 */
	public void setFinishOrder(int finishOrder)
    {
    	this.finishOrder = finishOrder;
		WizardCommand<T> command = getCommand(FINISH_COMMAND);
		if (command != null)
		{
			command.setOrder(this.finishOrder);
		}
    }
	
	/**
	 * @param nextLabel
	 */
	public void setNextLabel(String nextLabel)
	{
		this.nextLabel = nextLabel;
		WizardCommand<T> command = getCommand(NEXT_COMMAND);
		if (command != null)
		{
			command.setLabel(this.nextLabel);
		}
	}
	
	/**
	 * @param nextOrder
	 */
	public void setNextOrder(int nextOrder)
    {
    	this.nextOrder = nextOrder;
		WizardCommand<T> command = getCommand(NEXT_COMMAND);
		if (command != null)
		{
			command.setOrder(this.nextOrder);
		}
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardStepListener#stepChanged(br.com.sysmap.crux.widgets.client.wizard.Step, br.com.sysmap.crux.widgets.client.wizard.Step)
	 */
	public void stepChanged(Step<T> currentStep, Step<T> previousStep)
    {
		checkWizard();
		int currentOrder = wizard.getStepOrder(currentStep.getId());

		commands.get(BACK_COMMAND).setEnabled(hasEnabledPreviousStep(currentOrder));
		commands.get(NEXT_COMMAND).setEnabled(hasEnabledNextStep(currentOrder));

		updateStepCommands(currentStep);
    }

	/**
	 * @param wizard
	 */
	@Override
	protected void setWizard(Wizard<T> wizard)
    {
    	super.setWizard(wizard);
    	addDefaultCommands();
    }

	/**
	 * @param backLabel
	 * @param nextLabel
	 * @param cancelLabel
	 * @param finishLabel
	 */
	private void addDefaultCommands()
    {
		checkWizard();
		WizardCommand<T> command = new WizardCommand<T>(this, BACK_COMMAND, this.backOrder, backLabel, new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				if (!wizard.isChangingStep())
				{
					back();
				}
			}
		}, new WidgetWizardProxy<T>(wizard)); 
			
		command.addStyleDependentName("back");
		commands.put(BACK_COMMAND, command);
		
		command = new WizardCommand<T>(this, NEXT_COMMAND, this.nextOrder, nextLabel, new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				if (!wizard.isChangingStep())
				{
					next();
				}
			}
		}, new WidgetWizardProxy<T>(wizard)); 

		command.addStyleDependentName("next");
		commands.put(NEXT_COMMAND, command);

		command = new WizardCommand<T>(this, CANCEL_COMMAND, this.cancelOrder, cancelLabel, new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				cancel();
			}
		}, new WidgetWizardProxy<T>(wizard));
		
		command.addStyleDependentName("cancel");
		commands.put(CANCEL_COMMAND, command);

		command = new WizardCommand<T>(this, FINISH_COMMAND, this.finishOrder, finishLabel, new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				finish();
			}
		}, new WidgetWizardProxy<T>(wizard));
		
		command.addStyleDependentName("finish");
		commands.put(FINISH_COMMAND, command);
		
		updateCommands();
    }

	/**
	 * @param currentOrder
	 * @return
	 */
	private boolean hasEnabledNextStep(int currentOrder)
    {
		boolean ret = false;
		for (int i=currentOrder+1; i<(wizard.getStepCount()); i++)
		{
			if (wizard.isStepEnabled(i))
			{
				ret = true;
				break;
			}
		}
		
	    return ret;
    }
	
	/**
	 * @param currentOrder
	 * @return
	 */
	private boolean hasEnabledPreviousStep(int currentOrder)
    {
		boolean ret = false;
		for (int i=currentOrder-1; i>=0; i--)
		{
			if (wizard.isStepEnabled(i))
			{
				ret = true;
				break;
			}
		}
		
	    return ret;
    }

	/**
	 * 
	 */
	private void updateCommandButtons()
    {
		for (WizardCommand<T> command: commands.values())
		{
			command.setControlBar(this);
		}
    }

	/**
	 * 
	 */
	private void updateCommands()
    {
		List<WizardCommand<T>> sortedCommands = new ArrayList<WizardCommand<T>>();

		for (WizardCommand<T> command: commands.values())
		{
			sortedCommands.add(command);
		}

		Collections.sort(sortedCommands);
		
		int originalScrollPosition = (rollingPanel.isVertical()?rollingPanel.getVerticalScrollPosition():rollingPanel.getHorizontalScrollPosition());
		rollingPanel.clear();
		
		for (final WizardCommand<T> command: sortedCommands)
		{
			rollingPanel.add(command);
		}
		
		updateScrollPosition(originalScrollPosition);
    }

	/**
	 * @param currentStep
	 */
	private void updateStepCommands(Step<T> currentStep)
    {
		for (String command : stepCommands)
        {
			commands.remove(command);
        }
	    stepCommands.clear();
	    
	    Iterator<WizardCommand<T>> commands = currentStep.iterateCommands();
	    if (commands != null)
	    {
	    	while (commands.hasNext())
	    	{
	    		WizardCommand<T> command = commands.next();
	    		String commandId = command.getId();
	    		command.setControlBar(this);
	    		this.commands.put(commandId, command);
	    		stepCommands.add(commandId);
	    	}
		}
	    updateCommands();
    }

	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	public static class WizardCommand<T extends Serializable> extends Composite implements Comparable<WizardCommand<T>>, 
	                    HasWizardCommandHandlers<T>
	{
		private DecoratedButton button;
		private WizardControlBar<T> controlBar;
		private String id;
		private int order;
		private WizardProxy<T> proxy;
		
		WizardCommand(String id, int order, String label, WizardCommandHandler<T> commandHandler, WizardProxy<T> proxy)
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
		
		WizardCommand(WizardControlBar<T> controlBar, String id, int order, String label, WizardCommandHandler<T> commandHandler, 
				      WizardProxy<T> proxy)
		{
			this(id, order, label, commandHandler, proxy);
			setControlBar(controlBar);
		}
		
		public HandlerRegistration addWizardCommandHandler(WizardCommandHandler<T> handler)
		{
			return addHandler(handler, WizardCommandEvent.getType(proxy.getResource().getClass()));
		}

		public int compareTo(WizardCommand<T> o)
        {
			if (o == null)
			{
				return 1;
			}
	        return order==o.order?0:order<o.order?-1:1;
        }

		public String getId()
        {
        	return id;
        }

		public String getLabel()
        {
        	return button.getText();
        }

		public int getOrder()
        {
        	return order;
        }

		public boolean isEnabled()
        {
        	return this.button.isEnabled();
        }

		public void setEnabled(boolean enabled)
        {
        	this.button.setEnabled(enabled);
        }

		public void setLabel(String label)
        {
        	this.button.setText(label);
        }

		public void setOrder(int order)
        {
        	this.order = order;
        	if (controlBar != null)
        	{
        		controlBar.updateCommands();
        	}
        }
		
		void setControlBar(WizardControlBar<T> controlBar)
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

		public void addWizardCommandEvent(final Event commandEvent)
        {
	        addWizardCommandHandler(new WizardCommandHandler<T>()
			{
				public void onCommand(WizardCommandEvent<T> event)
                {
					Events.callEvent(commandEvent, event);
                }
			});
	        
        }
	}
}
