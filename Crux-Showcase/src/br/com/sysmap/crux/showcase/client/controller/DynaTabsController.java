package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.advanced.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.TextBox;

@Controller("dynaTabsController")
public class DynaTabsController {
	
	@Expose
	public void openNewTab(){
		DynaTabs tabs = Screen.get("dynaTabs", DynaTabs.class);
		String url = Screen.get("url", TextBox.class).getText();
		String tabId = new Date().getTime() + "";
		tabs.openTab(tabId, "New Tab!", url, true, false);
	}	
}