package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.showcase.client.dto.Person;
import br.com.sysmap.crux.widgets.client.wizard.EnterEvent;
import br.com.sysmap.crux.widgets.client.wizard.LeaveEvent;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;

@Controller("wizardStep4Controller")
public class WizardStep4Controller {
	
	@Create
	protected Person person;

	@Expose
	public void onEnter(EnterEvent event){
		Person personContext = event.getWizardAccessor().readContext(Person.class);
		if (personContext == null)
		{
			this.person = new Person();
		}
		else
		{
			this.person = personContext;
		}
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}

	@Expose
	public void onLeave(LeaveEvent event){
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
}