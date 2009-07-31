package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dialog.ProgressDialog;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller(value="sourcesController")
public class SourcesController {
	
	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadJavaSource() {
		
		ProgressDialog.show("Loading source...");
		
		String id = getScreenSimpleId();
		String javaFileName = id.substring(0,1).toUpperCase() + id.substring(1) + "Controller.java";
		
		service.getJavaFile(javaFileName, false, 
			new AsyncCallbackAdapter<String>(this){
				public void onComplete(String result)
				{
					TextArea textArea = Screen.get("javaSourceFrame", TextArea.class);
					textArea.getElement().setAttribute("wrap", "off");
					textArea.setValue(result);
					ProgressDialog.hide();
				}			
			}
		);
	}
	
	@Expose
	public void loadXmlSource() {
		
		ProgressDialog.show("Loading source...");
		
		String id = getScreenSimpleId();
		String xmlFileName = id + ".crux.xml";
		
		service.getXmlFile(xmlFileName, false, 
			new AsyncCallbackAdapter<String>(this){
				public void onComplete(String result)
				{
					TextArea textArea = Screen.get("xmlSourceFrame", TextArea.class);
					textArea.getElement().setAttribute("wrap", "off");
					textArea.setValue(result);
					ProgressDialog.hide();
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