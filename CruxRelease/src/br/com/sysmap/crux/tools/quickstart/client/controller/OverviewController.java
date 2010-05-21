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
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Controller("overviewController")
public class OverviewController
{

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
