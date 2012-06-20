package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.showcase.client.formatter.DateFormatter;
import org.cruxframework.crux.showcase.client.formatter.PhoneFormatter;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.user.client.ui.Label;

@Controller("maskedTextBoxController")
public class MaskedTextBoxController {
	
	@Create
	protected MaskedScreen screen;
	
	@Expose
	public void changeFormat(){
		
		Formatter formatter = screen.getMaskedTextBox().getFormatter();
		String label = null;
		
		if(formatter instanceof PhoneFormatter)	{
			formatter = new DateFormatter();
			label = "Date";
		}
		else {
			formatter = new PhoneFormatter();
			label = "Phone";
		}

		MaskedTextBox maskedTextBox = screen.getMaskedTextBox();
		maskedTextBox.setUnformattedValue(null);		
		maskedTextBox.setFormatter(formatter);
		
		screen.getMaskedLabel().setText(label);
	}
	
	public static interface MaskedScreen extends ScreenWrapper{
		MaskedTextBox getMaskedTextBox();
		Label getMaskedLabel();
	}
}