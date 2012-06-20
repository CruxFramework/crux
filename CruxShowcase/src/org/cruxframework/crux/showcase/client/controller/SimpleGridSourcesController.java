package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("simpleGridSourcesController")
public class SimpleGridSourcesController  extends BaseSourcesController implements SimpleGridSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/datasource/SimpleGridDataSource.java", "DataSource Class", true));
		aditionalTabs.add(new SourceTab("client/dto/Contact.java", "DTO Class", true));
		aditionalTabs.add(new SourceTab("server/SimpleGridServiceImpl.java", "Server Implementation", true));
		return aditionalTabs;
	}

}