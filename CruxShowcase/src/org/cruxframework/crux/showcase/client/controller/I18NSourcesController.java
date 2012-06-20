package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("i18nSourcesController")
public class I18NSourcesController extends BaseSourcesController implements I18NSourcesControllerCrossDoc {

	/**
	 * @see org.cruxframework.crux.showcase.client.controller.BaseSourcesController#getAdditionalSources()
	 */
	protected ArrayList<SourceTab> getAdditionalSources() {
		ArrayList<SourceTab> aditionalTabs = new ArrayList<SourceTab>();
		aditionalTabs.add(new SourceTab("client/controller/MyMessages.java", "Messages Interface (default locale)", true));
		aditionalTabs.add(new SourceTab("client/controller/MyMessages_pt_BR.properties", "Messages for pt_BR Locale (properties file)", true));
		return aditionalTabs;
	}
}