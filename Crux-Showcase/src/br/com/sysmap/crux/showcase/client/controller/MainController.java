package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

@Controller(value="controller")
public class MainController {
	
	@Expose
	public void sayHello() 
	{
		
		
		
		
		
		
		TextBox textBox = Screen.get("nameTextBox", TextBox.class);
		final String name = textBox.getValue();
		Label label = Screen.get("greetingLabel", Label.class);
		label.setText("Hello, " + name);		
	}
}