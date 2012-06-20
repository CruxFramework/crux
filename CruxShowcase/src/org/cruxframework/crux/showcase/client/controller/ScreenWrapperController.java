package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

@Controller("screenWrapperController")
public class ScreenWrapperController {
	
	@Create
	protected MyScreen screen;
	
	@Expose
	public void onClick(){
		
		Date tomorrow = new Date(new Date().getTime() + (24 * 60 * 60 * 1000));		
		
		screen.getDateOfBirth().setValue(tomorrow);
		screen.getName().setValue("Tom Morrow");
		screen.getPhone().setUnformattedValue(1234567890L);

	}
	
	@Expose
	public void onClearClick()
	{
		screen.getDateOfBirth().setValue(null);
		screen.getName().setValue("");
		screen.getPhone().setUnformattedValue(null);
	}
	
	/**
	 * Wrapper for the most used screen's widgets.
	 */
	public static interface MyScreen extends ScreenWrapper
	{
		TextBox getName();
		MaskedTextBox getPhone();
		DateBox getDateOfBirth();
	}
}