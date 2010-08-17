package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.crossdoc.TargetDocument;

@Controller("screenCommunicationController")
public class ScreenCommunicationController {
	
	@Create
	protected FrameControllerCrossDoc crossDoc;
	
	@Expose
	public void changeFrame()
	{
		((TargetDocument)crossDoc).setTargetFrame("myFrame");
		crossDoc.setMyLabel("Modified at " + new Date().toString());
	}
}