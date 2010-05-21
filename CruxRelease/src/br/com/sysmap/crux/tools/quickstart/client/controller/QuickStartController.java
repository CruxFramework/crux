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
package br.com.sysmap.crux.tools.quickstart.client.controller;

import com.google.gwt.user.client.Window;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.tools.quickstart.client.QuickStartMessages;
import br.com.sysmap.crux.tools.quickstart.client.dto.ProjectInfo;
import br.com.sysmap.crux.tools.quickstart.client.remote.QuickStartServiceAsync;
import br.com.sysmap.crux.tools.quickstart.client.screen.QuickStartScreen;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;
import br.com.sysmap.crux.widgets.client.event.OkEvent;
import br.com.sysmap.crux.widgets.client.event.OkHandler;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Controller("quickStartController")
public class QuickStartController
{
	@Create
	protected QuickStartScreen screen;
	
	@Create
	protected ProjectInfo projectInfo;

	@Create
	protected QuickStartServiceAsync service;
	
	@Create
	protected QuickStartMessages messages;

	@Expose
	public void onLoad()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
		service.getProjectInfoDefaultValues(new AsyncCallbackAdapter<ProjectInfo>(this)
		{
			@Override
            public void onComplete(ProjectInfo result)
            {
				projectInfo = result;
            }
		});
	}
	
	@Expose
	public void useModuleCheckChange()
	{
		screen.getProjectInfo().getRowFormatter().setVisible(5, 
				screen.getUseCruxModuleExtension().getValue());
	}
	
	@Expose
	public void finish()
	{
		service.generateProject(projectInfo, new AsyncCallbackAdapter<Boolean>(this)
		{
			@Override
            public void onComplete(Boolean result)
            {
				String title;
				String message;
				
				if (result!= null && result)
				{
					title = messages.generateAppSuccessTitle();
					message = messages.generateAppSuccessMessage();
				}
				else
				{
					title = messages.generateAppFailureTitle();
					message = messages.generateAppFailureMessage();
				}
				
				MessageBox.show(title, message, new OkHandler()
				{
					public void onOk(OkEvent event)
					{
						back();
					}
				});
            }
		});
	}

	@Expose
	public void back()
	{
		Window.Location.assign(Screen.appendDebugParameters("index.html"));
	}	

	@Expose
	public void onSummaryEnter()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}	

	@Expose
	public void onSummaryLeave()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}	
}
