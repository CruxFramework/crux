package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

@Controller("screenWrapperController")
public class ScreenWrapperController {
	@Create
	protected MyScreen screen;
	
	@Expose
	public void onClick(){
		Window.alert("Name: "+screen.getName().getValue());
		Window.alert("Phone: "+screen.getPhone().getUnformattedValue());
		Window.alert("Date of Birth: "+screen.getDateOfBirth().getValue());
		Window.alert("Another Date: "+screen.getDate2().getValue());
	}
	
	public static interface MyScreen extends ScreenWrapper
	{
		TextBox getName();
		MaskedTextBox getPhone();
		MaskedTextBox getDateOfBirth();
		DateBox getDate2();
		Button getButton();
	}
}