package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.grid.Grid;

@Controller("simpleGridController")
public class SimpleGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("simpleGrid", Grid.class);
		grid.loadData();
	}
}