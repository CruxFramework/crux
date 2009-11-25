package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("simpleGridSourcesController")
public class SimpleGridSourcesController extends SourcesController {
	
	private boolean contactLoaded;
	private boolean serviceImplLoaded;
	
	@Expose
	public void loadContact() {
		
		if(!contactLoaded)
		{
			contactLoaded = true;
			loadFile("client/dto/Contact.java", "contact");
		}
	}
	
	@Expose
	public void loadServiceImpl() {
		
		if(!serviceImplLoaded)
		{
			serviceImplLoaded = true;
			loadFile("server/SimpleGridServiceImpl.java", "serviceImpl");
		}
	}
}