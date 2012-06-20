package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.showcase.client.formatter.BirthdayFormatter;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.FinishEvent;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.LeaveEvent;
import org.cruxframework.crux.widgets.client.wizard.Wizard;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Controller("simpleWizardController")
public class SimpleWizardController {
	
	@Create
	protected WizardScreen screen;
	
    @Expose
	public void onCancel(final CancelEvent cancelEvt) {
    	((Wizard<?>) cancelEvt.getSource()).first();
		clearFields();
	}

	@SuppressWarnings("unchecked")
    @Expose
	public void onFinish(final FinishEvent finishEvt) {
		final Wizard<Person> wizard = (Wizard<Person>) finishEvt.getSource();
		String name = wizard.readData().getName();
		MessageBox.show(
			null, "Good Bye, " + name + "!", new OkHandler() {
				public void onOk(OkEvent ok) {
					wizard.first();
					clearFields();
				}
			}
		);
	}
	
	@Expose
	public void onEnterWelcomeStep(EnterEvent<Person> event) {
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(false);
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}

	@Expose
	public void onEnterPersonalInfoStep(EnterEvent<Person> event) {
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(true);
	}
	
	@Expose
	public void onLeavePersonalInfoStep(LeaveEvent<Person> event) {
		
		boolean movingForward = event.getNextStep().equals("thankYouStep");
		boolean missingName = movingForward && StringUtils.isEmpty(screen.getName().getValue());
		boolean missingBirthDate =  movingForward && screen.getDateOfBirth().getUnformattedValue() == null;
		
		if(missingName || missingBirthDate) {
			MessageBox.show("Oops...", "You must enter your first name and your date of birth before proceeding.", null);
			event.cancel();
		}
		
		else {
			
			String name = screen.getName().getValue();
			Long phone = (Long) screen.getPhone().getUnformattedValue();
			Date birth =  (Date) screen.getDateOfBirth().getUnformattedValue();
			
			Person person = new Person();
			person.setName(name);
			person.setPhone(phone != null ? phone.toString() : null);
			person.setDateOfBirth(birth);
			
			event.getWizardAccessor().updateData(person);
		}
	}
	
	@Expose
	public void onEnterThankYouStep(EnterEvent<Person> event) {
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
		Person person = event.getWizardAccessor().readData();
		String name = person.getName();
		String msg = "";
		if(person.getDateOfBirth() != null) {
			msg = "We will remember your birthday is " + new BirthdayFormatter().format(person.getDateOfBirth());
		}
		screen.getFinishMessage1().setText("Thanks for visiting, " + name + "!");
		screen.getFinishMessage2().setText(msg);
	}

	@Expose
	public void clearFields() {
		screen.getName().setValue(null);
		screen.getPhone().setUnformattedValue(null);
		screen.getDateOfBirth().setUnformattedValue(null);
	}
	
	/**
	 * Screen fields
	 */
	public static interface WizardScreen extends ScreenWrapper {
		Wizard<Person> getWizard();
		TextBox getName();
		MaskedTextBox getPhone();
		MaskedTextBox getDateOfBirth();
		Label getFinishMessage1();
		Label getFinishMessage2();
	}
}