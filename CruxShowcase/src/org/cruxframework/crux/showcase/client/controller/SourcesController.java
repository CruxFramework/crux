package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.remote.SVNServiceAsync;


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
			
			String id = getScreenSimpleId();
			String javaFileName = id.substring(0,1).toUpperCase() + id.substring(1) + "Controller.java";
			loadFile("client/controller/"+javaFileName, "javaSource");
		}
	}
	
	@Expose
	public void loadXmlSource() {
		
		if(!xmlLoaded)
		{
			xmlLoaded = true;
			
			String id = getScreenSimpleId();
			String xmlFileName = id + ".crux.xml";
			
			loadXMLFile(xmlFileName, "xmlSource");
		}
	}

	protected void loadFile(String sourceFile, final String widgetName)
	{
		Screen.blockToUser();		
		service.getJavaFile(sourceFile, false, 
			new AsyncCallbackAdapter<String>(this){
				public void onComplete(String result)
				{
					TextArea textArea = Screen.get(widgetName, TextArea.class);
					textArea.getElement().setAttribute("wrap", "off");
					textArea.setValue(result);
					Screen.unblockToUser();
				}		
				@Override
				public void onError(Throwable e)
				{
					Screen.unblockToUser();
					super.onError(e);
				}
			}
		);
	}
	
	protected void loadXMLFile(String sourceFile, final String widgetName)
	{
		Screen.blockToUser();		
		service.getXmlFile(sourceFile, false, 
			new AsyncCallbackAdapter<String>(this){
				public void onComplete(String result)
				{
					TextArea textArea = Screen.get(widgetName, TextArea.class);
					textArea.getElement().setAttribute("wrap", "off");
					textArea.setValue(result);
					Screen.unblockToUser();
				}		
				@Override
				public void onError(Throwable e)
				{
					Screen.unblockToUser();
					super.onError(e);
				}
			}
		);
	}

	private String getScreenSimpleId() {
		String id = Screen.getId();		
		int slash = id.lastIndexOf("/");
		int dot = id.lastIndexOf(".");
		id = id.substring(slash + 1, dot);
		return id;
	}
	
}