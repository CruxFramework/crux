package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("contextSourcesController")
public class ContextSourcesController extends SourcesController {
	
	private boolean xmlLoaded;
	private boolean frameControllerLoaded;
	private boolean contextInitializationControllerLoaded;
	
	@Expose
	public void loadFrameXML() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			loadXMLFile("contextFrame.crux.xml", "frameXml");
		}
	}
	
	@Expose
	public void loadFrameController() {
		
		if(!frameControllerLoaded)
		{
			frameControllerLoaded = true;
			loadFile("client/controller/ContextFrameController.java", "frameController");
		}
	}
	
	@Expose
	public void loadContextInitializer() {
		
		if(!contextInitializationControllerLoaded)
		{
			contextInitializationControllerLoaded = true;
			loadFile("client/controller/ContextInitializerController.java", "contextInitializerCntr");
		}
	}
}