package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("streamingGridSourcesController")
public class StreamingGridSourcesController extends BaseSourcesController implements StreamingGridSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/datasource/StreamingGridDataSource.java", "DataSource Class", true));
		aditionalTabs.add(new SourceTab("client/dto/Contact.java", "DTO Class", true));
		aditionalTabs.add(new SourceTab("server/StreamingGridServiceImpl.java", "Server Implementation", true));
		return aditionalTabs;
	}

}