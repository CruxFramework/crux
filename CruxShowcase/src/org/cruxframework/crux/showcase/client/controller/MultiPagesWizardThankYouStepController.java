package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.showcase.client.formatter.BirthdayFormatter;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.user.client.ui.Label;


@Controller("multiPagesWizardThankYouStepController")
public class MultiPagesWizardThankYouStepController {
	
	@Create
	protected WizardScreen screen;
	
	@Expose
	public void onEnter(EnterEvent<Person> event){
		
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
	
	/**
	 * Screen fields
	 */
	public static interface WizardScreen extends ScreenWrapper {
		Label getFinishMessage1();
		Label getFinishMessage2();
	}
}