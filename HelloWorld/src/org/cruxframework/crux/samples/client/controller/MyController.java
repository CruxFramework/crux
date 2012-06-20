package org.cruxframework.crux.samples.client.controller;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import org.cruxframework.crux.samples.client.remote.GreetingServiceAsync;

@Controller(value="myController")
public class MyController {
	
	@Create
	protected GreetingServiceAsync service; 
	
	@Expose
	public void sayHello() {
		
		TextBox textBox = Screen.get("nameTextBox", TextBox.class);
		final String name = textBox.getValue();
		
		service.getHelloMessage(name, new AsyncCallbackAdapter<String>(this){

				@Override
				public void onComplete(String result){
					Label label = Screen.get("greetingLabel", Label.class);
					label.setText(result);		
				}
			}
		);
	}
}