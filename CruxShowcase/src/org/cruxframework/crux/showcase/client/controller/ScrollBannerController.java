package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.scrollbanner.ScrollBanner;

@Controller("scrollBannerController")
public class ScrollBannerController {
	
	@Expose
	public void addMessages(){
		ScrollBanner scrollBanner = Screen.get("scrollingMessages", ScrollBanner.class);
		scrollBanner.addMessage("Another message. This one was added programmatically.");
		scrollBanner.addMessage("Like the second one, this message was also added programmatically.");
	}
}