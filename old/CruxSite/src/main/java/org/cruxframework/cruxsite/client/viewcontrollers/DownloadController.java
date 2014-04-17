package org.cruxframework.cruxsite.client.viewcontrollers;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.plugin.google.analytics.client.GoogleAnalytics;

@Controller("downloadController")
public class DownloadController {

	@Expose
	public void downloadCrux()
	{
		// Código de chamada do Analytics
		GoogleAnalytics.trackEvent("Download", "Crux");
	}
	
	@Expose
	public void mavenCrux()
	{
		// Código de chamada do Analytics
		GoogleAnalytics.trackEvent("Download", "MavenPOM");
	}
	
	@Expose
	public void sourceCrux()
	{
		// Código de chamada do Analytics
		GoogleAnalytics.trackEvent("Download", "Source");
	}
	
}
