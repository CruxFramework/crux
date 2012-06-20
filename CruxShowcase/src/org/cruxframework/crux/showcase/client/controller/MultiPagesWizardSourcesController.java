package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("multiPagesWizardSourcesController")
public class MultiPagesWizardSourcesController extends BaseSourcesController implements MultiPagesWizardSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("multiPagesWizardWelcomeStep.crux.xml", "1st Step Page", false));
		aditionalTabs.add(new SourceTab("client/controller/MultiPagesWizardWelcomeStepController.java", "1st Step Controller", true));
		aditionalTabs.add(new SourceTab("multiPagesWizardPersonalInfoStep.crux.xml", "2nd Step Page", false));
		aditionalTabs.add(new SourceTab("client/controller/MultiPagesWizardPersonalInfoStepController.java", "2nd Step Controller", true));
		aditionalTabs.add(new SourceTab("multiPagesWizardThankYouStep.crux.xml", "3rd Step Page", false));
		aditionalTabs.add(new SourceTab("client/controller/MultiPagesWizardThankYouStepController.java", "3rd Step Controller", true));
		return aditionalTabs;
	}
}