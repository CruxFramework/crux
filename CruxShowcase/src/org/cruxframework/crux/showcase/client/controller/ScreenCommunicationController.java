package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;

@Controller("screenCommunicationController")
public class ScreenCommunicationController {
	
	@Create
	protected FrameControllerCrossDoc crossDoc;
	
	@Create
	protected Person person;
	
	@Expose
	public void changeFrame()
	{
		((TargetDocument)crossDoc).setTargetFrame("myFrame");
		crossDoc.setPerson(person);
	}
}