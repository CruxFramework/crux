package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("noJavaSourcesController")
public class NoJavaSourcesController extends BaseSourcesController implements NoJavaSourcesControllerCrossDoc {

	@Override
	protected boolean hasControllerSource()
	{
		return false;
	}
}