package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.grid.Grid;

@Controller("streamingGridController")
public class StreamingGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("streamingGrid", Grid.class);
		grid.loadData();
	}
}