package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.LeaveEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.user.client.ui.TextBox;


@Controller("multiPagesWizardPersonalInfoStepController")
public class MultiPagesWizardPersonalInfoStepController implements MultiPagesWizardPersonalInfoStepControllerCrossDoc {
	
	@Create
	protected WizardScreen screen;
	
	@Expose
	public void onEnter(EnterEvent<Person> event){
		
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(true);
	}
	
	@Expose
	public void onLeave(LeaveEvent<Person> event) {
		
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
	public void clearFields() {
		screen.getName().setValue(null);
		screen.getPhone().setUnformattedValue(null);
		screen.getDateOfBirth().setUnformattedValue(null);
	}
	
	/**
	 * Screen fields
	 */
	public static interface WizardScreen extends ScreenWrapper {
		TextBox getName();
		MaskedTextBox getPhone();
		MaskedTextBox getDateOfBirth();
	}
}