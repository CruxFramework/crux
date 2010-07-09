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

import br.com.sysmap.crux.core.client.controller.crossdoc.TargetDocument;
import br.com.sysmap.crux.widgets.client.dynatabs.AbstractTab;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PageStep extends LazyPanel
{
	private final String id;
	private final String url;
	private CruxInternalWizardPageControllerCrossDoc wizardController = GWT.create(CruxInternalWizardPageControllerCrossDoc.class);

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
		EnterEvent result = new EnterEvent(null, previousStep);

		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));
		wizardController.onEnter(wizardId, previousStep);
		return result;
	}
	
	/**
	 * @return
	 */
	LeaveEvent fireLeaveEvent(String wizardId, String nextStep)
	{
		LeaveEvent result = new LeaveEvent(null, nextStep);

		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));
		if (!wizardController.onLeave(wizardId, nextStep))
		{
			result.cancel();
		}
		return result;
	}
	
	/**
	 * @return
	 */
	Iterator<WizardCommand> iterateWizardCommands(final String wizardId)
	{
		List<WizardCommand> result = new ArrayList<WizardCommand>();
		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));

		WizardCommandData[] commands =  wizardController.listCommands();
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
	
	/**
	 * @param wizardId
	 * @param commanddId
	 * @return
	 */
	private void fireCommandEvent(String wizardId, String commandId)
	{
		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));
		wizardController.onCommand(wizardId, commandId);
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
