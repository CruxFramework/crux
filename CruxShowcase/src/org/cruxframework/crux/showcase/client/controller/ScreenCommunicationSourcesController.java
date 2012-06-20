package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("screenCommunicationSourcesController")
public class ScreenCommunicationSourcesController extends BaseSourcesController implements ScreenCommunicationSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/controller/Person.java", "DTO Source", true));
		aditionalTabs.add(new SourceTab("innerFrame.crux.xml", "Inner Frame Page", false));
		aditionalTabs.add(new SourceTab("client/controller/FrameController.java", "Inner Frame Controller", true));
		aditionalTabs.add(new SourceTab("client/controller/FrameControllerCrossDoc.java", "Inner Frame CrossDoc Interface", true));
		return aditionalTabs;
	}
}