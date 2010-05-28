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

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Controller("examplesController")
public class ExamplesController
{
	@Expose
	public void openShowcase()
	{
		Window.Location.assign("http://crux-showcase.appspot.com/");
	}
	
	@Expose
	public void openHelloWorld()
	{
		Window.Location.assign("/helloworld/index.html");
	}	
	
	@Expose
	public void backToMainMenu()
	{
		Window.Location.assign("index.html");
	}
}