package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.controller.ContextInitializerController.SharedContext;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;


import com.google.gwt.user.client.ui.TextBox;

@Controller("contextController")
public class ContextController {
	
	@Create
	protected SharedContext context;
	
	@Expose
	public void save(){
		
		TextBox textBox = Screen.get("message", TextBox.class);
		
		String message = textBox.getText();
		context.setMessage(message);
		context.setDate(new Date());
		
		textBox.setText("");
		
		MessageBox.show("", "Your message was saved in context. Try reading it from inside the IFrame.", null);
	}	
}