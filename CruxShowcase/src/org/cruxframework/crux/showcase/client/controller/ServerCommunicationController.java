package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.remote.ServerCommunicationServiceAsync;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;

import com.google.gwt.user.client.ui.TextBox;

@Controller("serverCommunicationController")
public class ServerCommunicationController {
	
	@Create
	protected ServerCommunicationServiceAsync service;
	
	@Expose
	public void callService() {

		String name = Screen.get("name", TextBox.class).getValue();
		
		service.sayHello(name,
			
			new AsyncCallbackAdapter<String>(this){
			
				public void onComplete(String result){
					MessageBox.show("Greeting", result, null);
				}
				
				public void onError(Throwable e){
					MessageBox.show("Oops!", e.getMessage(), null);
				}
			}
		);
	}
}