package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabs;
import org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem;

import com.google.gwt.event.logical.shared.SelectionEvent;

@Controller("mainController")
public class MainController {
	
	@Expose
	public void openExample(SelectionEvent<StackMenuItem> evt) {
		StackMenuItem item = evt.getSelectedItem();
		Screen.get("tabs", DynaTabs.class).openTab(item.getKey(), item.getLabel(), item.getKey() + ".html", true, false);
	}
}