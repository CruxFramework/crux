package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("contextSourcesController")
public class ContextSourcesController extends BaseSourcesController implements ContextSourcesControllerCrossDoc {
	
	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/controller/ContextFrameController.java", "Internal Frame Controller", true));
		aditionalTabs.add(new SourceTab("client/controller/ContextInitializerController.java", "Context Initializer Controller", true));
		aditionalTabs.add(new SourceTab("contextFrame.crux.xml", "Internal Frame Page", false));
		return aditionalTabs;
	}	
}