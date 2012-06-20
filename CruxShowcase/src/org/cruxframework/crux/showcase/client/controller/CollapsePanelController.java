package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.collapsepanel.CollapsePanel;

@Controller("collapsePanelController")
public class CollapsePanelController {
	
	@Expose
	public void onBeforeExpand(){
		Screen.get("collapsePanel", CollapsePanel.class).setTitleText("Click the (-) icon to hide the contents");
	}
	
	@Expose
	public void onBeforeCollapse(){
		Screen.get("collapsePanel", CollapsePanel.class).setTitleText("Click the (+) icon to view the contents");
	}
}