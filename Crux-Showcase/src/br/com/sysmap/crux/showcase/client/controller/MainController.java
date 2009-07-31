package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dialog.MessageBox;
import br.com.sysmap.crux.advanced.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.basic.client.ExecuteEvent;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;

import com.google.gwt.user.client.ui.MenuBar;

@Controller(value="mainController")
public class MainController {
	
	private static final String HTML = ".html";
	private static final String DECORATED_BUTTON_TAB = "decoratedButton";
	
	@Create
	protected MainScreen screen;
	
	/**
	 * @param menuBar
	 */
	@Expose
	public void onClickMenuItem(ExecuteEvent<MenuBar> event){
		MessageBox.show("Info", "MenuItem was clicked!", null);
	}
	
	@Expose
	public void onClickDecoratedButtonItem(){
		screen.getTabs().openTab(DECORATED_BUTTON_TAB, "Decorated Button", DECORATED_BUTTON_TAB + HTML, true, false);				
	}
	
	protected static interface MainScreen extends ScreenWrapper {
		DynaTabs getTabs();
	}
}