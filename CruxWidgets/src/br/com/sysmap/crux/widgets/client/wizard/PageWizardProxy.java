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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.context.ContextManager;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
class PageWizardProxy implements WizardProxy
{
	private String wizardId;
	/**
	 * 
	 */
	PageWizardProxy(String wizardId)
    {
		this.wizardId = wizardId;
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#cancel()
	 */
	public void cancel()
    {
		invokeSimpleCommand("cancel", wizardId, Object.class);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#finish()
	 */
	public boolean finish()
    {
	    return invokeSimpleCommand("finish");
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#first()
	 */
	public boolean first()
    {
	    return invokeSimpleCommand("first");
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#getStepOrder(java.lang.String)
	 */
	public int getStepOrder(String id)
    {
	    return invokeSimpleCommand("getStepOrder", new Object[]{wizardId, id}, Integer.class);
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#next()
	 */
	public boolean next()
    {
	    return invokeSimpleCommand("next");
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#back()
	 */
	public boolean back()
    {
	    return invokeSimpleCommand("back");
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#selectStep(java.lang.String, boolean)
	 */
	public boolean selectStep(String id, boolean ignoreLeaveEvent)
    {
	    return invokeSimpleCommand("selectStep", new Object[]{wizardId, id, ignoreLeaveEvent}, Boolean.class);
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#updateContext(java.lang.Object)
	 */
	public void updateContext(Object data)
	{
        ContextManager.getContextHandler().writeData("__Wizard."+wizardId, data);
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#readContext(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
    public <T> T readContext(Class<T> dataType)
	{
        return (T)ContextManager.getContextHandler().readData("__Wizard."+wizardId);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardProxy#getControlBar()
	 */
	public WizardControlBarAccessor getControlBar()
    {
	    return new WizardControlBarAccessor(new WizardControlBarPageProxy(wizardId));
    }
	
	/**
	 * @param cmd
	 * @return
	 */
	private boolean invokeSimpleCommand(String cmd)
    {
		 Boolean ret = invokeSimpleCommand(cmd, wizardId, Boolean.class);
		 if (ret != null)
		 {
			 return ret;
		 }
		 return false;
    }
	
	/**
	 * @param cmd
	 * @param param
	 * @return
	 */
	private static <T> T invokeSimpleCommand(String cmd, Object param, Class<T> returnType)
    {
	    try
        {
	        return Screen.invokeControllerOnParent("__wizard."+cmd, param, returnType);
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError(WidgetMsgFactory.getMessages().wizardPageStepErrorInvokingEventOuterPage(), e); 
        }
        return null;
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	static class WizardControlBarPageProxy implements WizardControlBarProxy
	{
		private final String wizardId;

		public WizardControlBarPageProxy( String wizardId)
        {
			this.wizardId = wizardId;
        }

		public void cancel()
        {
		    invokeSimpleCommand("cancel");
        }

		public void finish()
        {
		    invokeSimpleCommand("finish");
        }

		public String getButtonsHeight()
        {
	        return PageWizardProxy.invokeSimpleCommand("getButtonsHeight", wizardId, String.class);
        }

		public String getButtonsStyle()
        {
	        return PageWizardProxy.invokeSimpleCommand("getButtonsStyle", wizardId, String.class);
        }

		public String getButtonsWidth()
        {
	        return PageWizardProxy.invokeSimpleCommand("getButtonsWidth", wizardId, String.class);
        }

		public String getCancelLabel()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCancelLabel", wizardId, String.class);
        }

		public String getFinishLabel()
        {
	        return PageWizardProxy.invokeSimpleCommand("getFinishLabel", wizardId, String.class);
        }

		public String getNextLabel()
        {
	        return PageWizardProxy.invokeSimpleCommand("getNextLabel", wizardId, String.class);
        }

		public String getBackLabel()
        {
	        return PageWizardProxy.invokeSimpleCommand("getBackLabel", wizardId, String.class);
        }

		public int getSpacing()
        {
	        return PageWizardProxy.invokeSimpleCommand("getSpacing", wizardId, Integer.class);
        }

		public boolean isVertical()
        {
	        return PageWizardProxy.invokeSimpleCommand("isVertical", wizardId, Boolean.class);
        }

		public void next()
        {
		    invokeSimpleCommand("next");
        }

		public void back()
        {
		    invokeSimpleCommand("back");
        }

		public void setButtonsHeight(String buttonHeight)
        {
	        PageWizardProxy.invokeSimpleCommand("setButtonsHeight", new Object[]{wizardId,buttonHeight}, Object.class);
        }

		public void setButtonsStyle(String buttonStyle)
        {
	        PageWizardProxy.invokeSimpleCommand("setButtonsStyle", new Object[]{wizardId,buttonStyle}, Object.class);
        }

		public void setButtonsWidth(String buttonWidth)
        {
	        PageWizardProxy.invokeSimpleCommand("setButtonsWidth", new Object[]{wizardId,buttonWidth}, Object.class);
        }

		public void setCancelLabel(String cancelLabel)
        {
	        PageWizardProxy.invokeSimpleCommand("setCancelLabel", new Object[]{wizardId,cancelLabel}, Object.class);
        }

		public void setFinishLabel(String finishLabel)
        {
	        PageWizardProxy.invokeSimpleCommand("setFinishLabel", new Object[]{wizardId,finishLabel}, Object.class);
        }

		public void setNextLabel(String nextLabel)
        {
	        PageWizardProxy.invokeSimpleCommand("setNextLabel", new Object[]{wizardId,nextLabel}, Object.class);
        }

		public void setBackLabel(String backLabel)
        {
	        PageWizardProxy.invokeSimpleCommand("setBackLabel", new Object[]{wizardId,backLabel}, Object.class);
        }

		public void setSpacing(int spacing)
        {
	        PageWizardProxy.invokeSimpleCommand("setSpacing", new Object[]{wizardId,spacing}, Object.class);
        }
		
		private void invokeSimpleCommand(String cmd)
	    {
			PageWizardProxy.invokeSimpleCommand(cmd, wizardId, Object.class);
	    }

		public WizardCommandAccessor getCommand(String commandId)
        {
	        return new WizardCommandAccessor(new WizardCommandPageProxy(wizardId, commandId));
        }
	}

	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	static class WizardCommandPageProxy implements WizardCommandProxy
	{
		private final String wizardId;
		private final String commandId;

		public WizardCommandPageProxy(String wizardId, String commandId)
        {
			this.wizardId = wizardId;
			this.commandId = commandId;
        }

		public String getId()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandId", new Object[]{wizardId,commandId}, String.class);
        }

		public String getLabel()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandLabel", new Object[]{wizardId,commandId}, String.class);
        }

		public int getOrder()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandOrder", new Object[]{wizardId,commandId}, Integer.class);
        }

		public boolean isEnabled()
        {
	        return PageWizardProxy.invokeSimpleCommand("isCommandEnabled", new Object[]{wizardId,commandId}, Boolean.class);
        }

		public void setEnabled(boolean enabled)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandEnabled", new Object[]{wizardId,commandId, enabled}, Object.class);
        }

		public void setLabel(String label)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandLabel", new Object[]{wizardId,commandId, label}, Object.class);
        }

		public void setOrder(int order)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandOrder", new Object[]{wizardId,commandId, order}, Object.class);
        }

		public String getStyleName()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandStyleName", new Object[]{wizardId,commandId}, String.class);
        }

		public void setStyleName(String styleName)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandStyleName", new Object[]{wizardId,commandId, styleName}, Object.class);
        }

		public int getOffsetHeight()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandOffsetHeight", new Object[]{wizardId,commandId}, Integer.class);
        }

		public int getOffsetWidth()
        {
	        return PageWizardProxy.invokeSimpleCommand("getCommandOffsetWidth", new Object[]{wizardId,commandId}, Integer.class);
        }

		public void setHeight(String height)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandHeight", new Object[]{wizardId,commandId, height}, Object.class);
        }

		public void setWidth(String width)
        {
	        PageWizardProxy.invokeSimpleCommand("setCommandWidth", new Object[]{wizardId,commandId, width}, Object.class);
        }
	}
}
