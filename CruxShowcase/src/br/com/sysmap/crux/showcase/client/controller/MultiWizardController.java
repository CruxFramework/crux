package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.FinishEvent;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;

import com.google.gwt.user.client.Window;

@Controller("multiWizardController")
public class MultiWizardController{
	
	@Expose
	public void onCancel(CancelEvent event){
		Window.alert("operation canceled! Returning to first step...");
		((Wizard)event.getSource()).first();
	}

	@Expose
	public void onFinish(FinishEvent event){
		Window.alert("Congratulations! Operation completed. Returnig to first step...");
		((Wizard)event.getSource()).first();
	}
}