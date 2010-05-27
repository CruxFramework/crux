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
import br.com.sysmap.crux.tools.quickstart.client.remote.WelcomeServiceAsync;
import br.com.sysmap.crux.tools.quickstart.client.screen.OverviewScreen;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Controller("overviewController")
public class OverviewController
{
	@Create
	protected OverviewScreen screen; 
	
	@Create
	protected QuickStartMessages messages; 

	@Create
	protected WelcomeServiceAsync service;
	
	@Expose
	public void onLoad()
	{
		service.getCruxVersion(new AsyncCallbackAdapter<String>(this)
		{
			@Override
			public void onComplete(String result)
			{
				screen.getVersionLabel().setText(messages.cruxVersion(result));
			}
		});
	}

	@Expose
	public void generateApp()
	{
		Window.Location.assign(Screen.appendDebugParameters("appWizard.html"));
	}
	
	@Expose
	public void viewExamples()
	{
		Window.Location.assign(Screen.appendDebugParameters("examples.html"));
	}
	
	@Expose
	public void viewJavadoc()
	{
		Window.Location.assign(Screen.appendDebugParameters("/docs/index.html"));
	}
	
	@Expose
	public void viewUserManual()
	{
		Window.Location.assign("http://code.google.com/p/crux-framework/wiki/UserManual");
	}
}
