package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;

@Controller("messageBoxController")
public class MessageBoxController {
	
	@Expose
	public void showMessage(){
		
		MessageBox.show(
				
			"A Memorable Quote from Mad Max Movie",
			
			"\"I'm a fuel injected suicide machine. I am the rocker, I am the roller, I am the out-of-controller!\"", 
			
			new OkHandler(){
				public void onOk(OkEvent event){
					MessageBox.show(null, "You've just closed the message box!", null);
				}
			}
		);
	}
}