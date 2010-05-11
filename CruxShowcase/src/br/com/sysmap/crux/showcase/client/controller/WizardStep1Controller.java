package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.widgets.client.wizard.EnterEvent;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;

@Controller("wizardStep1Controller")
public class WizardStep1Controller {
	
	@Expose
	public void onEnter(EnterEvent event){
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
}