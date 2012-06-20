package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.controller.ContextInitializerController.SharedContext;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;

import com.google.gwt.user.client.ui.Label;

@Controller("contextFrameController")
public class ContextFrameController {
	
	@Create
	protected SharedContext context;
	
	@Expose
	public void readContext(){
		
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
			MessageBox.show("", "Type a message and press 'Save in Context', before clicking here.", null);
		}
	}
}