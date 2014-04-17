package org.cruxframework.cruxsite.client.viewcontrollers;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.plugin.google.analytics.client.GoogleAnalytics;

@Controller("viewsController")
public class ViewsController {

	@Expose
	public void onActivate()
	{
		String viewName = View.of(this).getId();
		
		// Código de chamada do Analytics
		GoogleAnalytics.trackPageview(viewName);
	}
	
}
