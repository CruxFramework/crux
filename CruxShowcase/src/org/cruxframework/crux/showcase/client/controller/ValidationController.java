package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Validate;
import org.cruxframework.crux.core.client.event.ValidateException;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.CheckBox;

@Controller("validationController")
public class ValidationController {
	
	@Expose
	@Validate("ensureCheckbox")
	public void onClick(ClickEvent event) {
		MessageBox.show("", "The business method was successfully executed!", null);
	}
	
	public void ensureCheckbox(ClickEvent event) throws ValidateException {
		if (!Screen.get("checkBox", CheckBox.class).getValue()) {
			throw new ValidateException("You must ensure that checkbox is checked!");
		}
	}
}