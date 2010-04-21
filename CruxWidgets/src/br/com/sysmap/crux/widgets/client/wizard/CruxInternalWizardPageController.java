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

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.widgets.client.dynatabs.CruxInternalDynaTabsController;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Global
@Controller(value="__wizard", lazy=false)
public class CruxInternalWizardPageController extends CruxInternalDynaTabsController
{
	/**
	 * @param event
	 * @return
	 */
	@ExposeOutOfModule
	public boolean onLeave(InvokeControllerEvent event)
	{
		//TODO - Thiago - elaborar forma de saber o ID da widget <wizardPage> ...
		//registrar duas vezes a widget...uma com um id fixo e outra com o que estiver na tag... inclusive, assim da pra saber se existem dois na tela e 
		//reportar erro... fazer isso na widget e nao na sua factory... pra funfar tbm programaticamente.
		return false;
	}
}
