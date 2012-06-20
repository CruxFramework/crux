package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("sensitiveMethodSourcesController")
public class SensitiveMethodSourcesController extends BaseSourcesController implements SensitiveMethodSourcesControllerCrossDoc {
	
	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/remote/SensitiveServerService.java", "Service Interface", true));
		aditionalTabs.add(new SourceTab("server/SensitiveServerServiceImpl.java", "Service Implementation", true));
		return aditionalTabs;
	}
	
}