package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.dialog.Popup;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler;

import com.google.gwt.user.client.ui.TextBox;

@Controller("popupController")
public class PopupController {
	
	@Expose
	public void browse(){
		
		TextBox textBox = Screen.get("url", TextBox.class);

		String url = textBox.getText();		
		if(url == null || url.length() == 0)
		{
			url = "http://blog.cruxframework.org";
			textBox.setValue(url);
		}
		
		Popup.show(
			"Popup Example", url, 
			"700", "500", 
			new BeforeCloseHandler(){
				public void onBeforeClose(BeforeCloseEvent event){
					MessageBox.show("", "You've just closed the Popup!", null);
				}				
			},
			null,
			true,
			true
		);
	}
}