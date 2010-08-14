package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.Validate;
import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.showcase.client.dto.Address;
import br.com.sysmap.crux.showcase.client.dto.Person;
import br.com.sysmap.crux.widgets.client.wizard.EnterEvent;
import br.com.sysmap.crux.widgets.client.wizard.LeaveEvent;

import com.google.gwt.user.client.ui.TextBox;

@Controller("wizardStep3Controller")
public class WizardStep3Controller {
	@Create
	protected Person person;
	
	@Create
	protected StepScreen screen;
	
	@Expose
	public void onEnter(EnterEvent<Person> event){
		Person personContext = event.getWizardAccessor().readData();
		if (personContext == null) {
			clearFields();
		}
		else {
			this.person = personContext;
		}
	}

	@Expose
	@Validate
	public void onLeave(LeaveEvent<Person> event){
		event.getWizardAccessor().updateData(this.person);
	}
	
	public void validateOnLeave(LeaveEvent<Person> event) throws ValidateException{
		if (!"step2".equals(event.getNextStep()))
		{
			if (StringUtils.isEmpty(this.person.getAddress().getStreet())) {
				screen.getStreet().setFocus(true);
				event.cancel();
				throw new ValidateException("Field address is required!");
			}
			if (StringUtils.isEmpty(this.person.getAddress().getCity())) {
				screen.getCity().setFocus(true);
				event.cancel();
				throw new ValidateException("Field city is required!");
			}
			if (StringUtils.isEmpty(this.person.getAddress().getState())) {
				screen.getState().setFocus(true);
				event.cancel();
				throw new ValidateException("Field state is required!");
			}
		}
	}

	@Expose
	public void clearFields(){
		if (this.person == null)
		{
			this.person = new Person();
		}
		else
		{
			this.person.setAddress(new Address());
		}
	}
	
	public static interface StepScreen extends ScreenWrapper{
		TextBox getStreet();
		TextBox getCity();
		TextBox getState();
	}
	
}