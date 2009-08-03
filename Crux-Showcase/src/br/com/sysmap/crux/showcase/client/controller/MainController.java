package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;

@Controller("mainController")
public class MainController {
	
	private static final String HTML = ".html";
	private static final String DECORATED_BUTTON_TAB = "decoratedButton";
	private static final String TIMER_TAB = "timer";
	private static final String SCROLL_BANNER_TAB = "scrollBanner";
	private static final String TRANSFER_LIST_TAB = "transferList";
	private static final String  GXT_TAB = "gxt";
	private static final String  GWT_TAB = "gwt";
	private static final String  MASKED_TEXTBOX_TAB = "maskedTextBox";
	private static final String DECORATED_PANEL_TAB = "decoratedPanel";
	private static final String TITLE_PANEL_TAB = "titlePanel";
	private static final String COLLAPSE_PANEL_TAB = "collapsePanel";
	private static final String VALUE_BIND_TAB = "valueBind";
	private static final String STACK_MENU_TAB = "stackMenu";
	private static final String FILTER_TAB = "filter";
	
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
	
	@Expose
	public void onClickDecoratedPanelItem(){
		screen.getTabs().openTab(DECORATED_PANEL_TAB, "Decorated Panel", DECORATED_PANEL_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickTitlePanelItem(){
		screen.getTabs().openTab(TITLE_PANEL_TAB, "Title Panel", TITLE_PANEL_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickCollapsePanelItem(){
		screen.getTabs().openTab(COLLAPSE_PANEL_TAB, "Collapse Panel", COLLAPSE_PANEL_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickTransferListItem(){
		screen.getTabs().openTab(TRANSFER_LIST_TAB, "Transfer List", TRANSFER_LIST_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickStackMenuItem(){
		screen.getTabs().openTab(STACK_MENU_TAB, "Stack Menu", STACK_MENU_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickFilterItem(){
		screen.getTabs().openTab(FILTER_TAB, "Filter", FILTER_TAB + HTML, true, false);
	}

	@Expose
	public void onClickGxtItem(){
		screen.getTabs().openTab(GXT_TAB, "GXT Integration", GXT_TAB + HTML, true, false);
	}	
	
	@Expose
	public void onClickGwtItem(){
		screen.getTabs().openTab(GWT_TAB, "GWT Widgets", GWT_TAB + HTML, true, false);
	}	

	@Expose
	public void onClickMaskedTextBoxItem(){
		screen.getTabs().openTab(MASKED_TEXTBOX_TAB, "Masked Text Box", MASKED_TEXTBOX_TAB + HTML, true, false);
	}	
	
	@Expose
	public void onClickValueBindItem(){
		screen.getTabs().openTab(VALUE_BIND_TAB, "Value Bind", VALUE_BIND_TAB + HTML, true, false);
	}	
	
	protected static interface MainScreen extends ScreenWrapper {
		DynaTabs getTabs();
	}
}