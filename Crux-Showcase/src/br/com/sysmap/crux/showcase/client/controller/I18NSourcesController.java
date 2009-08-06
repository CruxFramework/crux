package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller("i18nSourcesController")
public class I18NSourcesController {
	
	private boolean messagesLoaded;
	private boolean propertiesLoaded;

	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadMessagesSource() {
		
		if(!messagesLoaded){
			messagesLoaded = true;
			
			loadFile("client/controller/MyMessages.java", "messagesSourceFrame");
		}
	}

	@Expose
	public void loadPropertiesSource() {
		
		if(!propertiesLoaded){
			propertiesLoaded = true;
			
			loadFile("client/controller/MyMessages_pt_BT.properties", "propertiesSourceFrame");
		}
	}

	private void loadFile(String sourceFile, final String widgetName)
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
}