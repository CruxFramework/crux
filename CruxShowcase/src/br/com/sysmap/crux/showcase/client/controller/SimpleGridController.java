package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.grid.impl.Grid;

@Controller("simpleGridController")
public class SimpleGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("simpleGrid", Grid.class);
		grid.loadData();
	}
}