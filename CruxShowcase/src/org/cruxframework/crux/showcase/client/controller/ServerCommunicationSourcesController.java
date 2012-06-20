package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("serverCommunicationSourcesController")
public class ServerCommunicationSourcesController  extends BaseSourcesController implements ServerCommunicationSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/remote/ServerCommunicationService.java", "Business Interface", true));
		aditionalTabs.add(new SourceTab("client/remote/ServerCommunicationServiceAsync.java", "Async Interface", true));
		aditionalTabs.add(new SourceTab("server/ServerCommunicationServiceImpl.java", "Service Implementation", true));
		aditionalTabs.add(new SourceTab("client/remote/PleaseTypeYourNameException.java", "Business Exception", true));
		return aditionalTabs;
	}
}