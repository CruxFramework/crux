package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("parameterBindSourcesController")
public class ParameterBindSourcesController extends BaseSourcesController implements ParameterBindSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/dto/SimpleContact.java", "Parameter DTO Source", true));
		aditionalTabs.add(new SourceTab("parameterBindPopup.crux.xml", "Popup Page Source", false));
		aditionalTabs.add(new SourceTab("client/controller/ParameterBindPopupController.java", "Popup Controller Source", true));
		return aditionalTabs;
	}
}