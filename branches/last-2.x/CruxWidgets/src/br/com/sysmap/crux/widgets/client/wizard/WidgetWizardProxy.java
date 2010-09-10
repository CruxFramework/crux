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

import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
class WidgetWizardProxy<T extends Serializable> implements WizardProxy<T>
{
	private Wizard<T> wizard;
	
	/**
	 * @param wizard
	 */
	WidgetWizardProxy(Wizard<T> wizard)
    {
		this.wizard = wizard;
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#back()
	 */
	public boolean back()
    {
	    int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep-1, true);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#cancel()
	 */
	public void cancel()
    {
	    wizard.cancel();
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#finish()
	 */
	public boolean finish()
    {
	    return wizard.finish();
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#first()
	 */
	public boolean first()
    {
		return wizard.selectStep(0, true);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#getControlBar()
	 */
    public WizardControlBarAccessor getControlBar()
    {
	    return new WizardControlBarAccessor(new WizardControlBarWidgetProxy<T>(wizard.getControlBar()));
    }

	public T getResource()
    {
	    return wizard.getResource();
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#getStepOrder(java.lang.String)
	 */
	public int getStepOrder(String id)
    {
	    return wizard.getStepOrder(id);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#next()
	 */
	public boolean next()
    {
	    int currentStep = wizard.getCurrentStepIndex();
		return wizard.selectStep(currentStep+1, true);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#readContext()
	 */
	public T readData()
    {
	    return wizard.readData();
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#selectStep(java.lang.String, boolean)
	 */
	public boolean selectStep(String id, boolean ignoreLeaveEvent)
    {
	    return wizard.selectStep(id, ignoreLeaveEvent);
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#updateData(java.lang.Object)
	 */
	public void updateData(T data)
    {
		wizard.updateData(data);
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	static class WizardCommandWidgetProxy<T extends Serializable> implements WizardCommandProxy
	{
		private final WizardCommand<T> command;

		public WizardCommandWidgetProxy(WizardCommand<T> command)
        {
			this.command = command;
        }

		public String getId()
        {
	        return command.getId();
        }

		public String getLabel()
        {
	        return command.getLabel();
        }

		public int getOffsetHeight()
        {
	        return command.getOffsetHeight();
        }

		public int getOffsetWidth()
        {
	        return command.getOffsetWidth();
        }

		public int getOrder()
        {
	        return command.getOrder();
        }

		public String getStyleName()
        {
	        return command.getStyleName();
        }

		public boolean isEnabled()
        {
	        return command.isEnabled();
        }

		public void setEnabled(boolean enabled)
        {
			command.setEnabled(enabled);
        }

		public void setHeight(String height)
        {
			command.setHeight(height);
        }

		public void setLabel(String label)
        {
			command.setLabel(label);
        }

		public void setOrder(int order)
        {
			command.setOrder(order);
        }

		public void setStyleName(String styleName)
        {
			command.setStyleName(styleName);
        }

		public void setWidth(String width)
        {
			command.setWidth(width);
        }
	}

	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	static class WizardControlBarWidgetProxy<T extends Serializable> implements WizardControlBarProxy
	{
		private final WizardControlBar<T> controlBar;

		public WizardControlBarWidgetProxy(WizardControlBar<T> controlBar)
        {
			this.controlBar = controlBar;
        }

		public void back()
        {
	        controlBar.back();
        }

		public void cancel()
        {
			controlBar.cancel();
        }

		public void finish()
        {
			controlBar.finish();
        }

		public String getBackLabel()
        {
	        return controlBar.getBackLabel();
        }

		public String getButtonsHeight()
        {
	        return controlBar.getButtonsHeight();
        }

		public String getButtonsStyle()
        {
	        return controlBar.getButtonsStyle();
        }

		public String getButtonsWidth()
        {
	        return controlBar.getButtonsWidth();
        }

		public String getCancelLabel()
        {
	        return controlBar.getCancelLabel();
        }

		public WizardCommandAccessor getCommand(String commandId)
        {
	        return new WizardCommandAccessor(new WizardCommandWidgetProxy<T>(controlBar.getCommand(commandId)));
        }

		public String getFinishLabel()
        {
	        return controlBar.getFinishLabel();
        }

		public String getNextLabel()
        {
	        return controlBar.getNextLabel();
        }

		public int getSpacing()
        {
	        return controlBar.getSpacing();
        }

		public boolean isVertical()
        {
	        return controlBar.isVertical();
        }

		public void next()
        {
	        controlBar.next();
        }

		public void setBackLabel(String backLabel)
        {
	        controlBar.setBackLabel(backLabel);
        }

		public void setButtonsHeight(String buttonHeight)
        {
	        controlBar.setButtonsHeight(buttonHeight);
        }

		public void setButtonsStyle(String buttonStyle)
        {
	        controlBar.setButtonsStyle(buttonStyle);
        }

		public void setButtonsWidth(String buttonWidth)
        {
	        controlBar.setButtonsWidth(buttonWidth);
        }

		public void setCancelLabel(String cancelLabel)
        {
	        controlBar.setCancelLabel(cancelLabel);
        }

		public void setFinishLabel(String finishLabel)
        {
	        controlBar.setFinishLabel(finishLabel);
        }

		public void setNextLabel(String nextLabel)
        {
	        controlBar.setNextLabel(nextLabel);
        }

		public void setSpacing(int spacing)
        {
	        controlBar.setSpacing(spacing);
        }
	}
}
