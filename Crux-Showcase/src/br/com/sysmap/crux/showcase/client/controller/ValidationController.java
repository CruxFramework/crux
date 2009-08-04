package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.Validate;
import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;

@Controller("validationController")
public class ValidationController {
	
	@Expose
	@Validate("validateMethod")
	public void onClick(){
		Window.alert("The controller method was executed!");
	}
	
	public void validateMethod() throws ValidateException
	{
		if (!Screen.get("checkBox", CheckBox.class).getValue())
		{
			throw new ValidateException("You must ensure that checkbox is checked!");
		}
	}
}