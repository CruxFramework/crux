package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.stackmenu.StackMenuItem;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Label;

@Controller("stackMenuController")
public class StackMenuController {
	
	@Expose
	public void onClickItem(){
		String clickedItem = Screen.get("menu", StackMenuItem.class).getLabel();
		Screen.get("message", Label.class).setText(clickedItem);
	}
}