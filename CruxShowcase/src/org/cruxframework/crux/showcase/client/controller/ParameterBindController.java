package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.Popup;

import com.google.gwt.http.client.URL;

@Controller("parameterBindController")
public class ParameterBindController {
	
	@Expose
	public void onClick(){
		
		String parameters = 
					"?name=" + URL.encodeQueryString("Belle Lee Button") 
					+ "&phone=" + 1234567890L 
					+ "&dateOfBirth=" + new Date().getTime();
		
		String url = "parameterBindPopup.html" + parameters;
		
		Popup.show("Parameter Bind", url, "800", "350", null, null, true, true);
	}
}