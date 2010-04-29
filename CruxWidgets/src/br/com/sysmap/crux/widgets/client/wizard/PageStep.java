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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.widgets.client.dynatabs.AbstractTab;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class PageStep extends LazyPanel
{
	private final String id;
	private final String url;

	/**
	 * @param id
	 * @param label
	 * @param url
	 */
	PageStep(String id, String url)
    {
		this.id = id;
		this.url = url;
    }

	@Override
    protected Widget createWidget()
    {
	    return new WizardPageTab(id, url);
    }

	public String getId()
    {
    	return id;
    }

	public String getUrl()
    {
    	return url;
    }

	/**
	 * @param previousStep
	 * @return
	 */
	EnterEvent fireEnterEvent(String wizardId, String previousStep)
	{
		try
        {
	        EnterEvent result = new EnterEvent(null, previousStep);
			CruxInternalWizardPageController.invokeOnTab(getId(), "__wizard.onEnter", new Object[]{wizardId, previousStep});
			return result;
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError("", e); // TODO - Thiago - message
        }
		
		return null;
	}
	
	/**
	 * @return
	 */
	LeaveEvent fireLeaveEvent(String wizardId)
	{
		try
        {
	        LeaveEvent result = new LeaveEvent(null);
	        
			if (!CruxInternalWizardPageController.invokeOnTab(getId(), "__wizard.onLeave", wizardId, Boolean.class))
			{
		        result.cancel();
			}
			return result;
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError("", e); // TODO - Thiago - message
        }
		
		return null;
	}
	
	/**
	 * @return
	 */
	Iterator<WizardCommand> iterateWizardCommands(final String wizardId)
	{
		try
        {
			List<WizardCommand> result = new ArrayList<WizardCommand>();
			WizardCommandData[] commands =  CruxInternalWizardPageController.invokeOnTab(getId(), "__wizard.listCommands", null, WizardCommandData[].class);
			if (commands != null)
			{
				for (final WizardCommandData data : commands)
                {
	                result.add(new WizardCommand(data.getId(), data.getOrder(), data.getLabel(), new WizardCommandHandler()
					{
						public void onCommand(WizardCommandEvent event)
						{
							fireCommandEvent(wizardId, data.getId());
						}
					}, new PageWizardProxy(wizardId)));
                }
			}
			return result.iterator();
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError("", e); // TODO - Thiago - message
        }
		
		return null;
	}
	
	/**
	 * @param wizardId
	 * @param commanddId
	 * @return
	 */
	private void fireCommandEvent(String wizardId, String commandId)
	{
		try
        {
			CruxInternalWizardPageController.invokeOnTab(getId(), "__wizard.onCommand", new Object[]{wizardId, commandId});
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError("", e); // TODO - Thiago - message
        }
	}
	
	static class WizardPageTab extends AbstractTab
	{
		/**
		 * @param id
		 * @param label
		 * @param url
		 */
		WizardPageTab(String id, String url)
	    {
		    super(id, url);
		    getFrame().setWidth("100%");
	    }		
	}
}
