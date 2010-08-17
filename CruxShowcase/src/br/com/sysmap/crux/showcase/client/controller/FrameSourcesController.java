package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("frameSourcesController")
public class FrameSourcesController extends SourcesController{
	
	private boolean xmlLoaded;
	private boolean frameController;
	private boolean frameCrossDoc;
	
	@Expose
	public void loadXmlSource() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			loadXMLFile("innerFrame.crux.xml", "frameSource");
		}
	}

	@Expose
	public void loadFrameControllerSource() {
		
		if(!frameController)
		{
			frameController = true;
			loadFile("client/controller/FrameController.java", "frameControllerSource");
		}
	}
	
	@Expose
	public void loadFrameCrossDocSource() {
		
		if(!frameCrossDoc)
		{
			frameCrossDoc = true;
			loadFile("client/controller/FrameControllerCrossDoc.java", "frameCrossDocSource");
		}
	}
	
}