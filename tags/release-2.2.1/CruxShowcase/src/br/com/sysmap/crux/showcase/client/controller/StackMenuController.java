package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Label;

@Controller("stackMenuController")
public class StackMenuController {
	
	@Expose
	public void onClickItem_1_1(){
		Screen.get("message", Label.class).setText("Item 1.1 was clicked!");
	}
	
	@Expose
	public void onClickItem_1_2_1(){
		Screen.get("message", Label.class).setText("Item 1.2.1 was clicked!");
	}
	
	@Expose
	public void onClickItem_1_2_2(){
		Screen.get("message", Label.class).setText("Item 1.2.2 was clicked!");
	}
	
	@Expose
	public void onClickItem_1_2_3(){
		Screen.get("message", Label.class).setText("Item 1.2.3 was clicked!");
	}
	
	
	@Expose
	public void onClickItem_2(){
		Screen.get("message", Label.class).setText("Item 2 was clicked!");
	}

	@Expose
	public void onClickItem_3(){
		Screen.get("message", Label.class).setText("Item 3 was clicked!");
	}
	
}