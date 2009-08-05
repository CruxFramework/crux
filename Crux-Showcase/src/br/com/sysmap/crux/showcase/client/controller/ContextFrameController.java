package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.controller.ContextInitializer.SharedContext;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

@Controller("contextFrameController")
public class ContextFrameController {
	
	@Create
	protected SharedContext context;
	
	@Expose
	public void readModifiedDate(){
		
		String message = context.getMessage();
		Date msgDate = context.getDate();
		
		if(message != null)
		{
			long messageAge = (new Date().getTime() - msgDate.getTime()) / 1000;
			
			Screen.get("label", Label.class).setText(
				"Message read from context: '" + message + "'. Message age: " + messageAge + " seconds."
			);
		}
		else
		{
			Window.alert("Type a message and press 'Save', before clicking here.");
		}
	}
}