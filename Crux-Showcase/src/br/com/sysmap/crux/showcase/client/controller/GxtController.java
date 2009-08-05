package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.SimplePanel;

@Controller("gxtController")
public class GxtController {
	
	@Expose
	public void loadGXTWidgets(){
		SimplePanel simplePanel = Screen.get("gxtWidgets", SimplePanel.class);
		simplePanel.add(new GxtExample());
	}
	
	public class GxtExample extends LayoutContainer {  

		public GxtExample()
		{
			Button button = new Button("GXT Button on Crux");
			add(button);
		} 
	} 
}