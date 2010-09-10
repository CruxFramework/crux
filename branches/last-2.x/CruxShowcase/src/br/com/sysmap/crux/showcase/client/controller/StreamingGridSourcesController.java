package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;

@Controller("streamingGridSourcesController")
public class StreamingGridSourcesController extends AbstractGridSourcesController {
	
	@Override
	protected String getDtoFilePath()
	{
		return "Contact.java";
	}

	@Override
	protected String getServiceImplFilePath()
	{
		return "StreamingGridServiceImpl.java";
	}
	
	@Override
	protected String getDSFilePath()
	{
		return "StreamingGridDataSource.java";
	}	
}