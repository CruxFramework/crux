package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.scrollbanner.ScrollBanner;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

@Controller("scrollBannerController")
public class ScrollBannerController {
	
	@Expose
	public void addMessages(){
		ScrollBanner scrollBanner = Screen.get("scrollingMessages", ScrollBanner.class);
		scrollBanner.addMessage("Another message. This one was added programmatically.");
		scrollBanner.addMessage("Like the second one, this message was also was added programmatically.");
	}
}