package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller("frameSourcesController")
public class FrameSourcesController {
	
	private boolean xmlLoaded;
	
	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadXmlSource() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			
			Screen.blockToUser();
			String xmlFileName = "innerFrame.crux.xml";
			
			service.getXmlFile(xmlFileName, false, 
				new AsyncCallbackAdapter<String>(this){
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("frameSourceFrame", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}			
				}
			);
		}
	}
}