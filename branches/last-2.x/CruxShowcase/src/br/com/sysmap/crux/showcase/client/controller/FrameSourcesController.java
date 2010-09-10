package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("frameSourcesController")
public class FrameSourcesController extends SourcesController{
	
	private boolean xmlLoaded;
	
	@Expose
	public void loadXmlSource() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			loadXMLFile("innerFrame.crux.xml", "frameSource");
		}
	}
}