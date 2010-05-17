package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.FinishEvent;
import br.com.sysmap.crux.widgets.client.event.OkEvent;
import br.com.sysmap.crux.widgets.client.event.OkHandler;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;

@Controller("multiWizardController")
public class MultiWizardController{
	
	@Expose
	public void onCancel(final CancelEvent cancelEvt){
		final Wizard wizard = (Wizard) cancelEvt.getSource();
		MessageBox.show(
			"Info", "Operation canceled! Returning to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}

	@Expose
	public void onFinish(final FinishEvent finishEvt){
		final Wizard wizard = (Wizard) finishEvt.getSource();
		MessageBox.show(
			"Info", "Congratulations! Operation completed. Returnig to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}
}