package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;

@Controller(value="mainController")
public class MainController {
	
	private static final String HTML = ".html";
	private static final String DECORATED_BUTTON_TAB = "decoratedButton";
	private static final String TIMER_TAB = "timer";
	private static final String  SCROLL_BANNER_TAB = "scrollBanner";
	
	@Create
	protected MainScreen screen;
	
	@Expose
	public void onClickDecoratedButtonItem(){
		screen.getTabs().openTab(DECORATED_BUTTON_TAB, "Decorated Button", DECORATED_BUTTON_TAB + HTML, true, false);				
	}
	
	@Expose
	public void onClickTimerItem(){
		screen.getTabs().openTab(TIMER_TAB, "Timer", TIMER_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickScrollBannerItem(){
		screen.getTabs().openTab(SCROLL_BANNER_TAB, "Scroll Banner", SCROLL_BANNER_TAB + HTML, true, false);
	}	
	
	protected static interface MainScreen extends ScreenWrapper {
		DynaTabs getTabs();
	}
}