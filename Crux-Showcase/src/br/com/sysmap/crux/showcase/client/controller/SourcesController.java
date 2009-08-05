package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller("sourcesController")
public class SourcesController {
	
	private boolean xmlLoaded;
	private boolean javaLoaded;
	
	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadJavaSource() {
		
		if(!javaLoaded)
		{
			javaLoaded = true;
			
			Screen.blockToUser();
			
			String id = getScreenSimpleId();
			String javaFileName = id.substring(0,1).toUpperCase() + id.substring(1) + "Controller.java";
			
			service.getJavaFile(javaFileName, false, 
				new AsyncCallbackAdapter<String>(this){
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("javaSourceFrame", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}
				}
			);
		}
	}
	
	@Expose
	public void loadXmlSource() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			
			Screen.blockToUser();
		
			String id = getScreenSimpleId();
			String xmlFileName = id + ".crux.xml";
			
			service.getXmlFile(xmlFileName, false, 
				new AsyncCallbackAdapter<String>(this){
				
					public void onComplete(String result)
					{
						TextArea textArea = Screen.get("xmlSourceFrame", TextArea.class);
						textArea.getElement().setAttribute("wrap", "off");
						textArea.setValue(result);
						Screen.unblockToUser();
					}
					
					@Override
					public void onError(Throwable e)
					{
						Screen.unblockToUser();
					}
				}
			);
		}
	}

	private String getScreenSimpleId() {
		String id = Screen.getId();		
		int slash = id.lastIndexOf("/");
		int dot = id.lastIndexOf(".");
		id = id.substring(slash + 1, dot);
		return id;
	}
	
}