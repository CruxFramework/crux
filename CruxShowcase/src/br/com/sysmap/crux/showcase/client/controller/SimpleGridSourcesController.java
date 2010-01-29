package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;

@Controller("simpleGridSourcesController")
public class SimpleGridSourcesController extends AbstractGridSourcesController {
	
	@Override
	protected String getDtoFilePath()
	{
		return "Contact.java";
	}

	@Override
	protected String getServiceImplFilePath()
	{
		return "SimpleGridServiceImpl.java";
	}
}