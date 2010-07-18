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
public class PageStep<T extends Serializable> extends LazyPanel
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
	EnterEvent<T> fireEnterEvent(Wizard<T> wizard, String previousStep)
	{
		EnterEvent<T> result = new EnterEvent<T>(null, previousStep);

		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));
		wizardController.onEnter(wizard.getElement().getId(), previousStep);
		return result;
	}
	
	/**
	 * @return
	 */
	LeaveEvent<T> fireLeaveEvent(Wizard<T> wizard, String nextStep)
	{
		LeaveEvent<T> result = new LeaveEvent<T>(null, nextStep);

		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));
		if (!wizardController.onLeave(wizard.getElement().getId(), nextStep))
		{
			result.cancel();
		}
		return result;
	}
	
	/**
	 * @return
	 */
	Iterator<WizardCommand<T>> iterateWizardCommands(final String wizardId)
	{
		List<WizardCommand<T>> result = new ArrayList<WizardCommand<T>>();
		((TargetDocument)wizardController).setTargetWindow(CruxInternalWizardPageController.getTabWindow(getId()));

		WizardCommandData[] commands =  wizardController.listCommands();
		if (commands != null)
		{
			for (final WizardCommandData data : commands)
			{
				result.add(new WizardCommand<T>(data.getId(), data.getOrder(), data.getLabel(), new WizardCommandHandler<T>()
				{
					public void onCommand(WizardCommandEvent<T> event)
					{
						fireCommandEvent(wizardId, data.getId());
					}
				}, new PageWizardProxy<T>(wizardId)));
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
