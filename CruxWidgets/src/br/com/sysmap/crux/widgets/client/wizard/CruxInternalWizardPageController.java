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

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.WidgetController;
import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabsControllerInvoker;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("__wizard")
@WidgetController({	Wizard.class, WizardPage.class })
public class CruxInternalWizardPageController extends DynaTabsControllerInvoker implements CruxInternalWizardPageControllerCrossDoc
{
    /**
	 * @param event
	 * @return
	 */
	public boolean onLeave(String nextStep)
	{
		WizardPage<?> wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			LeaveEvent<?> leaveEvent = wizardPage.fireLeaveEvent(nextStep);
			return !leaveEvent.isCanceled();
		}
		
		return true;
	}

	/**
	 * @param event
	 * @return
	 */
	public void onEnter(String previousStep)
	{
		WizardPage<?> wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			wizardPage.fireEnterEvent(previousStep);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	public void onCommand(String commandId)
	{
		WizardPage<?> wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
		if (wizardPage != null)
		{
			wizardPage.fireCommandEvent(commandId);
		}
	}

	/**
	 * @return
	 */
	public WizardCommandData[] listCommands()
	{
		WizardPage<?> wizardPage = Screen.get(WizardPage.PAGE_UNIQUE_ID, WizardPage.class);
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
	public void cancel(String wizardId)
	{
		Screen.get(wizardId, Wizard.class).cancel();
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean finish(String wizardId)
	{
		return Screen.get(wizardId, Wizard.class).finish();
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean first(String wizardId)
	{
		return Screen.get(wizardId, Wizard.class).selectStep(0, true);
	}

	/**
	 * @param event
	 * @return
	 */
	public int getStepOrder(String wizardId, String stepId)
	{
		return Screen.get(wizardId, Wizard.class).getStepOrder(stepId);
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean next(String wizardId)
	{
	    Wizard<?> wizard = Screen.get(wizardId, Wizard.class);
		int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep+1, true);
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean back(String wizardId)
	{
	    Wizard<?> wizard = Screen.get(wizardId, Wizard.class);
		int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep-1, true);
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean selectStep(String wizardId, String stepId, boolean ignoreLeaveEvents)
	{
		return Screen.get(wizardId, Wizard.class).selectStep(stepId, ignoreLeaveEvents);
	}

	/**
	 * @param event
	 * @return
	 */
	public String getButtonsHeight(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getButtonsStyle(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getButtonsWidth(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getCancelLabel(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getFinishLabel(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getNextLabel(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public String getBackLabel(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public int getSpacing(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public boolean isVertical(String wizardId)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
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
	public void setButtonsHeight(String wizardId, String buttonHeight)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsHeight(buttonHeight);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	public void setButtonsStyle(String wizardId, String buttonStyle)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsStyle(buttonStyle);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	public void setButtonsWidth(String wizardId, String buttonWidth)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setButtonsWidth(buttonWidth);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	public void setCancelLabel(String wizardId, String cancelLabel)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setCancelLabel(cancelLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setFinishLabel(String wizardId, String finishLabel)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setFinishLabel(finishLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setNextLabel(String wizardId, String nextLabel)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setNextLabel(nextLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setBackLabel(String wizardId, String backLabel)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setBackLabel(backLabel);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setSpacing(String wizardId, int spacing)
	{
		WizardControlBar<?> controlBar = Screen.get(wizardId, Wizard.class).getControlBar();
		if (controlBar != null)
		{
			controlBar.setSpacing(spacing);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public String getCommandId(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public String getCommandLabel(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public int getCommandOrder(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public boolean isCommandEnabled(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public void setCommandEnabled(String wizardId, String commandId, boolean enabled)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setEnabled(enabled);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setCommandLabel(String wizardId, String commandId, String label)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setLabel(label);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setCommandOrder(String wizardId, String commandId, int order)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setOrder(order);
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	public String getCommandStyleName(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public int getOffsetHeight(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public int getOffsetWidth(String wizardId, String commandId)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
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
	public void setCommandStyleName(String wizardId, String commandId, String styleName)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setStyleName(styleName);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setCommandHeight(String wizardId, String commandId, String height)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setHeight(height);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public void setCommandWidth(String wizardId, String commandId, String width)
	{
		WizardCommand<?> command = Screen.get(wizardId, Wizard.class).getControlBar().getCommand(commandId);
		if (command != null)
		{
			 command.setWidth(width);
		}
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
