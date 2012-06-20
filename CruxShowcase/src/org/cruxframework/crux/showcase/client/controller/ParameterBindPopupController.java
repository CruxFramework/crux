package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.dto.SimpleContact;
import org.cruxframework.crux.widgets.client.maskedlabel.MaskedLabel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Controller("parameterBindPopupController")
public class ParameterBindPopupController {
	
	@Create
	protected SimpleContact contact;
	
	@Expose
	public void onLoad() {

		Screen.get("name", Label.class).setText(contact.getName());
		Screen.get("phone", MaskedLabel.class).setUnformattedValue(contact.getPhone());
		Screen.get("dateOfBirth", MaskedLabel.class).setUnformattedValue(contact.getDateOfBirth());
		Screen.get("urlValue", TextBox.class).setText(Window.Location.getHref());		
	}
}