package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.CheckBox;

@Controller("timerController")
public class TimerController {
	
	@Expose
	public void afterThreeSeconds(){
		Screen.get("threeSecondsCheckBox", CheckBox.class).setValue(true);
	}
	
	@Expose
	public void afterTenSeconds(){
		Screen.get("tenSecondsCheckBox", CheckBox.class).setValue(true);
	}
}