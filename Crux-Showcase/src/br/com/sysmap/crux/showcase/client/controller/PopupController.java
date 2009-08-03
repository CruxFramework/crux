package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dialog.Popup;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Controller("popupController")
public class PopupController {
	
	@Expose
	public void browse(){
		
		String url = Screen.get("url", TextBox.class).getText();
		
		if(url == null)
		{
			url = "http://www.google.com";
		}
		
		Popup.show(		
			"Popup Example", 
			url,			
			new CloseHandler<Popup>(){
				public void onClose(CloseEvent<Popup> event)
				{
					Screen.get("message", Label.class).setText("Popup was closed!");
				}				
			}
		);
	}
}