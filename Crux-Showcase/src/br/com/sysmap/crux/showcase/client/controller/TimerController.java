package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.CheckBox;

@Controller(value="timerController")
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