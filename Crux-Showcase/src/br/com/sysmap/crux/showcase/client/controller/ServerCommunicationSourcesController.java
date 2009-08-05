package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.remote.SVNServiceAsync;

import com.google.gwt.user.client.ui.TextArea;

@Controller("serverCommunicationSourcesController")
public class ServerCommunicationSourcesController {
	
	private boolean serviceLoaded;
	private boolean serviceAsyncLoaded;
	private boolean serviceImplLoaded;

	@Create
	protected SVNServiceAsync service;
	
	@Expose
	public void loadServiceSource() {
		
		if(!serviceLoaded){
			serviceLoaded = true;
			
			loadFile("client/remote/ServerService.java", "serviceSourceFrame");
		}
	}

	@Expose
	public void loadServiceAsyncSource() {
		
		if(!serviceAsyncLoaded){
			serviceAsyncLoaded = true;
			
			loadFile("client/remote/ServerServiceAsync.java", "serviceAsyncSourceFrame");
		}
	}

	@Expose
	public void loadServiceImplSource() {
		
		if(!serviceImplLoaded){
			serviceImplLoaded = true;
			
			loadFile("server/ServerServiceImpl.java", "serviceImplSourceFrame");
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