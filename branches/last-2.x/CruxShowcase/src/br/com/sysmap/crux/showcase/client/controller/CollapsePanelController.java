package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.collapsepanel.CollapsePanel;

@Controller("collapsePanelController")
public class CollapsePanelController {
	
	@Expose
	public void onBeforeExpand(){
		Screen.get("collapsePanel", CollapsePanel.class).setTitleText("Click the \"-\" icon to hide the contents");
	}
	
	@Expose
	public void onBeforeCollapse(){
		Screen.get("collapsePanel", CollapsePanel.class).setTitleText("Click the \"+\" icon to view the contents");
	}
}