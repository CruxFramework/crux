package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("maskedLabelSourcesController")
public class MaskedLabelSourcesController extends BaseSourcesController implements MaskedLabelSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/formatter/BirthDateFormatter.java", "Birth Date Formatter", true));
		aditionalTabs.add(new SourceTab("client/formatter/HeightFormatter.java", "Height Formatter", true));
		aditionalTabs.add(new SourceTab("client/formatter/WeightFormatter.java", "Weight Formatter", true));
		return aditionalTabs;
	}
}