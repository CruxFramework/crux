package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dialog.MessageBox;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvent;
import br.com.sysmap.crux.advanced.client.event.dialog.OkHandler;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Label;

@Controller("messageBoxController")
public class MessageBoxController {
	
	@Expose
	public void showMessage(){
		
		MessageBox.show(
				
			"Message For You",
			
			"The truth is out there.", 
			
			new OkHandler(){
				public void onOk(OkEvent event)
				{
					Screen.get("message", Label.class).setText("You accepted the message.");
				}
			}
		);
	}
}