package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabs;

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