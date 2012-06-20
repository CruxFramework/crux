/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.wizard;

import java.io.Serializable;

import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabsControllerInvoker;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
class PageWizardProxy<T extends Serializable> implements WizardProxy<T>
{
	private static CruxInternalWizardPageControllerCrossDoc wizardController = null;

	private String wizardId; 
	private WizardDataSerializer<T> wizardDataSerializer;

	/**
	 * 
	 */
	PageWizardProxy(String wizardId, WizardDataSerializer<T> wizardDataSerializer)
    {
		if (wizardController == null)
		{
			wizardController = GWT.create(CruxInternalWizardPageControllerCrossDoc.class);
			((TargetDocument)wizardController).setTarget(Target.PARENT);
		}
		this.wizardId = wizardId;
		this.wizardDataSerializer = wizardDataSerializer;
    }
	
	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#back()
	 */
	public boolean back()
    {
	    return wizardController.back(wizardId);
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#cancel()
	 */
	public void cancel()
    {
		wizardController.cancel(wizardId);
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#finish()
	 */
	public boolean finish()
    {
		return wizardController.finish(wizardId);
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#first()
	 */
	public boolean first()
    {
	    return wizardController.first(wizardId);
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#getControlBar()
	 */
	public WizardControlBarAccessor getControlBar()
    {
	    return new WizardControlBarAccessor(new WizardControlBarPageProxy(wizardId));
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#getStepOrder(java.lang.String)
	 */
	public int getStepOrder(String id)
    {
	    return wizardController.getStepOrder(wizardId, id);
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#next()
	 */
	public boolean next()
    {
	    return wizardController.next(wizardId);
    }
	
	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#readData(java.lang.Class)
	 */
    public T readData()
	{
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
	    return wizardDataSerializer.readObject();
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#selectStep(java.lang.String, boolean)
	 */
	public boolean selectStep(String id, boolean ignoreLeaveEvent)
    {
	    return wizardController.selectStep(wizardId, id, ignoreLeaveEvent);
    }
	
	@Override
    public void setStepEnabled(int stepOrder, boolean enabled)
    {
		wizardController.setStepEnabled(wizardId, stepOrder, enabled);
	}

	@Override
    public void setStepEnabled(String stepId, boolean enabled)
    {
		wizardController.setStepEnabled(wizardId, stepId, enabled);
    }
	
	
	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardProxy#updateContext(T)
	 */
	public void updateData(T data)
	{
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
	    wizardDataSerializer.writeObject(data);
	}

	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	static class WizardCommandPageProxy implements WizardCommandProxy
	{
		private final String commandId;
		private final String wizardId;

		public WizardCommandPageProxy(String wizardId, String commandId)
        {
			this.wizardId = wizardId;
			this.commandId = commandId;
        }

		public String getId()
        {
	        return wizardController.getCommandId(wizardId, commandId);
        }

		public String getLabel()
        {
	        return wizardController.getCommandLabel(wizardId, commandId);
        }

		public int getOffsetHeight()
        {
	        return wizardController.getOffsetHeight(wizardId, commandId);
        }

		public int getOffsetWidth()
        {
	        return wizardController.getOffsetWidth(wizardId, commandId);
        }

		public int getOrder()
        {
	        return wizardController.getCommandOrder(wizardId, commandId);
        }

		public String getStyleName()
        {
	        return wizardController.getCommandStyleName(wizardId, commandId);
        }

		public boolean isEnabled()
        {
	        return wizardController.isCommandEnabled(wizardId, commandId);
        }

		public void setEnabled(boolean enabled)
        {
			wizardController.setCommandEnabled(wizardId, commandId, enabled);
        }

		public void setHeight(String height)
        {
			wizardController.setCommandHeight(wizardId, commandId, height);
        }

		public void setLabel(String label)
        {
			wizardController.setCommandLabel(wizardId, commandId, label);
        }

		public void setOrder(int order)
        {
			wizardController.setCommandOrder(wizardId, commandId, order);
        }

		public void setStyleName(String styleName)
        {
			wizardController.setCommandStyleName(wizardId, commandId, styleName);
        }

		public void setWidth(String width)
        {
			wizardController.setCommandWidth(wizardId, commandId, width);
        }
	}

	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	static class WizardControlBarPageProxy implements WizardControlBarProxy
	{
		private final String wizardId;

		public WizardControlBarPageProxy( String wizardId)
        {
			this.wizardId = wizardId;
        }

		public void back()
        {
			wizardController.back(wizardId);
        }

		public void cancel()
        {
			wizardController.cancel(wizardId);
        }

		public void finish()
        {
			wizardController.finish(wizardId);
        }

		public String getBackLabel()
        {
	        return wizardController.getBackLabel(wizardId);
        }

		public String getButtonsHeight()
        {
	        return wizardController.getButtonsHeight(wizardId);
        }

		public String getButtonsStyle()
        {
	        return wizardController.getButtonsStyle(wizardId);
        }

		public String getButtonsWidth()
        {
	        return wizardController.getButtonsWidth(wizardId);
        }

		public String getCancelLabel()
        {
	        return wizardController.getCancelLabel(wizardId);
        }

		public WizardCommandAccessor getCommand(String commandId)
        {
	        return new WizardCommandAccessor(new WizardCommandPageProxy(wizardId, commandId));
        }

		public String getFinishLabel()
        {
	        return wizardController.getFinishLabel(wizardId);
        }

		public String getNextLabel()
        {
	        return wizardController.getNextLabel(wizardId);
        }

		public int getSpacing()
        {
	        return wizardController.getSpacing(wizardId);
        }

		public void next()
        {
			wizardController.next(wizardId);
        }

		public void setBackLabel(String backLabel)
        {
			wizardController.setBackLabel(wizardId, backLabel);
        }

		public void setButtonsHeight(String buttonHeight)
        {
			wizardController.setButtonsHeight(wizardId, buttonHeight);
        }

		public void setButtonsStyle(String buttonStyle)
        {
			wizardController.setButtonsStyle(wizardId, buttonStyle);
        }

		public void setButtonsWidth(String buttonWidth)
        {
			wizardController.setButtonsWidth(wizardId, buttonWidth);
        }

		public void setCancelLabel(String cancelLabel)
        {
			wizardController.setCancelLabel(wizardId, cancelLabel);
        }

		public void setFinishLabel(String finishLabel)
        {
			wizardController.setFinishLabel(wizardId, finishLabel);
        }

		public void setNextLabel(String nextLabel)
        {
			wizardController.setNextLabel(wizardId, nextLabel);
        }
		
		public void setSpacing(int spacing)
        {
			wizardController.setSpacing(wizardId, spacing);
        }
	}

	public T getResource()
    {
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
	    return wizardDataSerializer.getResource();
    }
	
	public JSWindow getStepWindow(String stepId) 
	{
		return DynaTabsControllerInvoker.getSiblingTabWindow(stepId);
	}
}
