package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.controller.ContextInitializer.SharedContext;

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
	}	
}