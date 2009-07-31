package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.gxt.GxtExample;

import com.google.gwt.user.client.ui.SimplePanel;

@Controller("gxtController")
public class GxtController {
	
	@Expose
	public void loadGXTWidgets(){
		SimplePanel simplePanel = Screen.get("gxtWidgets", SimplePanel.class);
		simplePanel.add(new GxtExample());
	}
}