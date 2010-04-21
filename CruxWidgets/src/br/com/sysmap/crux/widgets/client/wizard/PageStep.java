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
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.widgets.client.dynatabs.AbstractTab;
import br.com.sysmap.crux.widgets.client.event.step.EnterEvent;
import br.com.sysmap.crux.widgets.client.event.step.LeaveEvent;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class PageStep extends AbstractTab
{
	private static CruxInternalWizardPageController pageController = null;
	
	PageStep(String id, String label, String url)
    {
	    super(id, label, url);
    }

	EnterEvent fireEnterEvent(String previousStep)
	{
		return null;
	}
	
	LeaveEvent fireLeaveEvent()
	{
/*		try
        {
	        LeaveEvent result = new LeaveEvent();
	        
	      //TODO - Thiago - param
			if (!CruxInternalWizardPageController.invokeOnTab(getId(), "__wizard.onLeave", "param", Boolean.class))
			{
		        result.cancel();
			}
        }
        catch (ModuleComunicationException e)
        {
        	Crux.getErrorHandler().handleError("", e); // TODO - Thiago - message
        }
*/		
		return null;
	}
	
	private static CruxInternalWizardPageController getController()
	{
		if (pageController == null)
		{
			pageController = new CruxInternalWizardPageController();
		}
		return pageController;
	}
}
