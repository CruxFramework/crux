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

import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.event.ControllerInvoker;
import br.com.sysmap.crux.core.client.event.EventProcessor;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabsControllerInvoker;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class CruxInternalWizardPageController extends DynaTabsControllerInvoker implements ControllerInvoker
{
	/**
	 * 
	 */
	public CruxInternalWizardPageController()
    {
		ModuleComunicationSerializer serializer = Screen.getCruxSerializer();
		serializer.registerCruxSerializable(WizardCommandData.class.getName(), new WizardCommandData());
    }
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean onLeave(InvokeControllerEvent event)
	{
		WizardPage wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			String wizardId = (String) event.getParameter(0);
			String nextStep = (String)event.getParameter(1);
			LeaveEvent leaveEvent = LeaveEvent.fire(wizardPage, new PageWizardProxy(wizardId), nextStep);
			return !leaveEvent.isCanceled();
		}
		
		return true;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void onEnter(InvokeControllerEvent event)
	{
		WizardPage wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			String wizardId = (String) event.getParameter(0);
			String previousStep = (String)event.getParameter(1);
			EnterEvent.fire(wizardPage, new PageWizardProxy(wizardId), previousStep);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void onCommand(InvokeControllerEvent event)
	{
		WizardPage wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			String wizardId = (String) event.getParameter(0);
			String commandId = (String)event.getParameter(1);
			WizardCommandEvent wizardCommandEvent = new WizardCommandEvent(new PageWizardProxy(wizardId));
			wizardPage.fireCommandEvent(commandId, wizardCommandEvent);
		}
	}

	/**
	 * @return
	 */
	@ExposeOutOfModule
	public WizardCommandData[] listCommands()
	{
		WizardPage wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			return wizardPage.listCommands();
		}
		return null;
		
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void cancel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		Screen.get(wizardId, Wizard.class).cancel();
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean finish(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		return Screen.get(wizardId, Wizard.class).finish();
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean first(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		return Screen.get(wizardId, Wizard.class).selectStep(0, true);
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public int getStepOrder(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String stepId = (String) event.getParameter(1);
		return Screen.get(wizardId, Wizard.class).getStepOrder(stepId);
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean next(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
	    Wizard wizard = Screen.get(wizardId, Wizard.class);
		int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep+1, true);
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean back(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
	    Wizard wizard = Screen.get(wizardId, Wizard.class);
		int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep-1, true);
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean selectStep(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String stepId = (String) event.getParameter(1);
		Boolean ignoreLeaveEvents = (Boolean) event.getParameter(2);
		return Screen.get(wizardId, Wizard.class).selectStep(stepId, ignoreLeaveEvents);
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getButtonsHeight(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getButtonsHeight();
		}
		return null;
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getButtonsStyle(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getButtonsStyle();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getButtonsWidth(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getButtonsWidth();
		}
		return null;
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getCancelLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getCancelLabel();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getFinishLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getFinishLabel();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getNextLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getNextLabel();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getBackLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getBackLabel();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public int getSpacing(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.getSpacing();
		}
		return 0;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean isVertical(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			return controlBar.isVertical();
		}
		return false;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setButtonsHeight(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String buttonHeight = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsHeight(buttonHeight);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setButtonsStyle(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String buttonStyle = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsStyle(buttonStyle);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setButtonsWidth(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String buttonWidth = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsWidth(buttonWidth);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCancelLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String cancelLabel = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setCancelLabel(cancelLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setFinishLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String finishLabel = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setFinishLabel(finishLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setNextLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String nextLabel = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setNextLabel(nextLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setBackLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String backLabel = (String) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setBackLabel(backLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setSpacing(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		Integer spacing = (Integer) event.getParameter(1);
		WizardControlBar controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setSpacing(spacing);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getCommandId(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getId();
		}
		return null;
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getCommandLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getLabel();
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public int getCommandOrder(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getOrder();
		}
		return 0;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean isCommandEnabled(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.isEnabled();
		}
		return false;
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandEnabled(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		Boolean enabled = event.getParameter(2, Boolean.class);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setEnabled(enabled);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandLabel(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		String label = (String) event.getParameter(2);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setLabel(label);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandOrder(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		Integer order = (Integer) event.getParameter(2);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setOrder(order);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public String getCommandStyleName(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getStyleName();
		}
		return null;
	}	

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public int getOffsetHeight(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getOffsetHeight();
		}
		return 0;
	}	
	
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public int getOffsetWidth(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			return command.getOffsetWidth();
		}
		return 0;
	}	

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandStyleName(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		String styleName = (String) event.getParameter(2);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setStyleName(styleName);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandHeight(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		String height = (String) event.getParameter(2);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setHeight(height);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public void setCommandWidth(InvokeControllerEvent event)
	{
		String wizardId = (String) event.getParameter(0);
		String commandId = (String) event.getParameter(1);
		String width = (String) event.getParameter(2);
		WizardCommand command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setWidth(width);
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.event.ControllerInvoker#invoke(java.lang.String, java.lang.Object, boolean, br.com.sysmap.crux.core.client.event.EventProcessor)
	 */
	public void invoke(String method, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception
	{
		Object returnValue = null;
		boolean hasReturn = false;

		try
		{
			if ("selectStep".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = selectStep((InvokeControllerEvent)sourceEvent);
			}
			else if("back".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = back((InvokeControllerEvent)sourceEvent);
			}
			else if("next".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = next((InvokeControllerEvent)sourceEvent);
			}
			else if("getStepOrder".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getStepOrder((InvokeControllerEvent)sourceEvent);
			}
			else if("first".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = first((InvokeControllerEvent)sourceEvent);
			}
			else if("finish".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = finish((InvokeControllerEvent)sourceEvent);
			}
			else if("listCommands".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = listCommands();
			}
			else if("cancel".equals(method) && fromOutOfModule)
			{
				cancel((InvokeControllerEvent)sourceEvent);
			}
			else if("onCommand".equals(method) && fromOutOfModule)
			{
				onCommand((InvokeControllerEvent)sourceEvent);
			}
			else if("onEnter".equals(method) && fromOutOfModule)
			{
				onEnter((InvokeControllerEvent)sourceEvent);
			}
			else if("onLeave".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = onLeave((InvokeControllerEvent)sourceEvent);
			}
			else if("getButtonsHeight".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getButtonsHeight((InvokeControllerEvent)sourceEvent);
			}
			else if("getButtonsStyle".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getButtonsStyle((InvokeControllerEvent)sourceEvent);
			}
			else if("getButtonsWidth".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getButtonsWidth((InvokeControllerEvent)sourceEvent);
			}
			else if("getCancelLabel".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getCancelLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("getFinishLabel".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getFinishLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("getNextLabel".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getNextLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("getBackLabel".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getBackLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("getSpacing".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getSpacing((InvokeControllerEvent)sourceEvent);
			}
			else if("isVertical".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = isVertical((InvokeControllerEvent)sourceEvent);
			}
			else if("setButtonsHeight".equals(method) && fromOutOfModule)
			{
				setButtonsHeight((InvokeControllerEvent)sourceEvent);
			}
			else if("setButtonsStyle".equals(method) && fromOutOfModule)
			{
				setButtonsStyle((InvokeControllerEvent)sourceEvent);
			}
			else if("setButtonsWidth".equals(method) && fromOutOfModule)
			{
				setButtonsWidth((InvokeControllerEvent)sourceEvent);
			}
			else if("setCancelLabel".equals(method) && fromOutOfModule)
			{
				setCancelLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("setFinishLabel".equals(method) && fromOutOfModule)
			{
				setFinishLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("setNextLabel".equals(method) && fromOutOfModule)
			{
				setNextLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("setBackLabel".equals(method) && fromOutOfModule)
			{
				setBackLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("setSpacing".equals(method) && fromOutOfModule)
			{
				setSpacing((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandEnabled".equals(method) && fromOutOfModule)
			{
				setCommandEnabled((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandLabel".equals(method) && fromOutOfModule)
			{
				setCommandLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandOrder".equals(method) && fromOutOfModule)
			{
				setCommandOrder((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandStyleName".equals(method) && fromOutOfModule)
			{
				setCommandStyleName((InvokeControllerEvent)sourceEvent);
			}
			else if("isCommandEnabled".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = isCommandEnabled((InvokeControllerEvent)sourceEvent);
			}
			else if("getCommandOrder".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getCommandOrder((InvokeControllerEvent)sourceEvent);
			}
			else if("getCommandLabel".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getCommandLabel((InvokeControllerEvent)sourceEvent);
			}
			else if("getCommandId".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getCommandId((InvokeControllerEvent)sourceEvent);
			}
			else if("getCommandStyleName".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getCommandStyleName((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandHeight".equals(method) && fromOutOfModule)
			{
				setCommandHeight((InvokeControllerEvent)sourceEvent);
			}
			else if("setCommandWidth".equals(method) && fromOutOfModule)
			{
				setCommandWidth((InvokeControllerEvent)sourceEvent);
			}
			else if("getOffsetHeight".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getOffsetHeight((InvokeControllerEvent)sourceEvent);
			}
			else if("getOffsetWidth".equals(method) && fromOutOfModule)
			{
				hasReturn = true;
				returnValue = getOffsetWidth((InvokeControllerEvent)sourceEvent);
			}
		}
		catch (Throwable e)
		{
			eventProcessor.setException(e);
		} 

		if (hasReturn)
		{
			eventProcessor.setHasReturn(true);
			eventProcessor.setReturnValue(returnValue);
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#isAutoBindEnabled()
	 */
	public boolean isAutoBindEnabled()
    {
	    return false;
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#updateControllerObjects()
	 */
	public void updateControllerObjects()
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#updateScreenWidgets()
	 */
	public void updateScreenWidgets()
    {
    }
	
	/**
	 * @param pageId
	 * @return
	 * @throws ModuleComunicationException
	 */
	static boolean isInternalPageLoaded(String pageId) throws ModuleComunicationException
	{
		Element pageIFrame = getTabInternalFrameElement(pageId);
		if(pageIFrame == null)
		{
			throw new ModuleComunicationException(WidgetMsgFactory.getMessages().tabsControllerNoTabFound(pageId));
		}
		JSWindow window = retrieveTabWindow(pageIFrame);
		return existsAccessorFunction(window);
	}
	
	private static native boolean existsAccessorFunction(JSWindow tabWindow)/*-{
		return typeof tabWindow['_cruxScreenControllerAccessor'] == 'function';
	}-*/;
}
