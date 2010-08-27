package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.showcase.client.dto.Person;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.FinishEvent;
import br.com.sysmap.crux.widgets.client.event.OkEvent;
import br.com.sysmap.crux.widgets.client.event.OkHandler;
import br.com.sysmap.crux.widgets.client.maskedtextbox.MaskedTextBox;
import br.com.sysmap.crux.widgets.client.wizard.EnterEvent;
import br.com.sysmap.crux.widgets.client.wizard.LeaveEvent;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.user.client.ui.TextBox;

@Controller("simpleWizardController")
public class SimpleWizardController {
	
	@Create
	protected WizardScreen screen;
	
	@Create
	protected Person person;
	
	@SuppressWarnings("unchecked")
    @Expose
	public void onCancel(final CancelEvent cancelEvt){
		final Wizard<Person> wizard = (Wizard<Person>) cancelEvt.getSource();
		MessageBox.show(
			"Info", "Operation canceled! Returning to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}

	@SuppressWarnings("unchecked")
    @Expose
	public void onFinish(final FinishEvent finishEvt){
		final Wizard<Person> wizard = (Wizard<Person>) finishEvt.getSource();
		MessageBox.show(
			"Info", "Congratulations! Operation completed. Returnig to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}
	@Expose
	public void onEnterStep1(EnterEvent<Person> event)
	{
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(false);
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}

	@Expose
	public void onEnterStep2(EnterEvent<Person> event){
		
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(true);
	}
	
	@Expose
	public void onEnterStep3(EnterEvent<Person> event)
	{
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}

	@Expose
	public void onLeaveStep3(LeaveEvent<Person> event)
	{
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}

	@Expose
	public void clearFields(){
		this.person = new Person();
	}
	
	public static interface WizardScreen extends ScreenWrapper
	{
		Wizard<Person> getWizard();
		TextBox getName();
		MaskedTextBox getPhone();
		MaskedTextBox getDateOfBirth();		
	}
}