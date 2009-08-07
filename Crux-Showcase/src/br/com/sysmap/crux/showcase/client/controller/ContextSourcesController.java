package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller("contextSourcesController")
public class ContextSourcesController {
	
	private boolean xmlLoaded;
	private boolean frameControllerLoaded;
	private boolean contextInitializationControllerLoaded;
	
	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadFrameXML() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			
			Screen.blockToUser();
			
			String fileName = "contextFrame.crux.xml";
			
			service.getXmlFile(fileName, false, 
					
				new AsyncCallbackAdapter<String>(this){
				
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("frameXml", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}
					
					public void onError(Throwable e)
					{
						Screen.unblockToUser();
					}
				}
			);
		}
	}
	
	@Expose
	public void loadFrameController() {
		
		if(!frameControllerLoaded)
		{
			frameControllerLoaded = true;
			
			Screen.blockToUser();
			
			String fileName = "ContextFrameController.java";
			
			service.getJavaControllerFile(fileName, false, 
					
				new AsyncCallbackAdapter<String>(this){
				
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("frameController", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}
					
					public void onError(Throwable e)
					{
						Screen.unblockToUser();
					}
				}
			);
		}
	}
	
	@Expose
	public void loadContextInitializer() {
		
		if(!contextInitializationControllerLoaded)
		{
			contextInitializationControllerLoaded = true;
			
			Screen.blockToUser();
			
			String fileName = "ContextInitializerController.java";
			
			service.getJavaControllerFile(fileName, false, 
					
				new AsyncCallbackAdapter<String>(this){
				
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("contextInitializerCntr", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}
					
					public void onError(Throwable e)
					{
						Screen.unblockToUser();
					}
				}
			);
		}
	}
}