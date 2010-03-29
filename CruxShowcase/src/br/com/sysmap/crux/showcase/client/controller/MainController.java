package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabs;

@Controller("mainController")
public class MainController {
	
	private static final String HTML = ".html";
	private static final String DECORATED_BUTTON_TAB = "decoratedButton";
	private static final String TIMER_TAB = "timer";
	private static final String SCROLL_BANNER_TAB = "scrollBanner";
	private static final String TRANSFER_LIST_TAB = "transferList";
	private static final String GWT_TAB = "gwt";
	private static final String MASKED_TEXTBOX_TAB = "maskedTextBox";
	private static final String MASKED_LABEL_TAB = "maskedLabel";
	private static final String DECORATED_PANEL_TAB = "decoratedPanel";
	private static final String TITLE_PANEL_TAB = "titlePanel";
	private static final String COLLAPSE_PANEL_TAB = "collapsePanel";
	private static final String VALUE_BIND_TAB = "valueBind";
	private static final String PARAMETER_BIND_TAB = "parameterBind";
	private static final String STACK_MENU_TAB = "stackMenu";
	private static final String FILTER_TAB = "filter";
	private static final String SCREEN_COMMUNICATION_TAB = "screenCommunication";
	private static final String SCREEN_WRAPPER_TAB = "screenWrapper";
	private static final String DYNA_TABS_TAB = "dynaTabs";
	private static final String CONFIRM_TAB = "confirm";
	private static final String MESSAGE_BOX_TAB = "messageBox";	
	private static final String POPUP_TAB = "popup";
	private static final String PROGRESS_DIALOG_TAB = "progressDialog";
	private static final String VALIDATION_TAB = "validation";
	private static final String SERVER_COMMUNICATION_TAB = "serverCommunication";
	private static final String CONTEXT_TAB = "context";
	private static final String I18N_TAB = "i18n";
	private static final String SENSITIVE_METHOD_TAB = "sensitiveMethod";
	private static final String SIMPLE_GRID_TAB = "simpleGrid";
	private static final String STREAMING_GRID_TAB = "streamingGrid";
	private static final String WIDGET_GRID_TAB = "widgetGrid";
	private static final String GRID_EVENTS_TAB = "gridEvents";
	
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
	public void onClickGwtItem(){
		screen.getTabs().openTab(GWT_TAB, "GWT Widgets", GWT_TAB + HTML, true, false);
	}	

	@Expose
	public void onClickMaskedTextBoxItem(){
		screen.getTabs().openTab(MASKED_TEXTBOX_TAB, "Masked Text Box", MASKED_TEXTBOX_TAB + HTML, true, false);
	}	
	
	@Expose
	public void onClickMaskedLabelItem(){
		screen.getTabs().openTab(MASKED_LABEL_TAB, "Masked Label", MASKED_LABEL_TAB + HTML, true, false);
	}	
	
	@Expose
	public void onClickValueBindItem(){
		screen.getTabs().openTab(VALUE_BIND_TAB, "Value Bind", VALUE_BIND_TAB + HTML, true, false);
	}	
	
	@Expose
	public void onClickParameterBindItem(){
		screen.getTabs().openTab(PARAMETER_BIND_TAB, "Parameter Bind", PARAMETER_BIND_TAB + HTML+"?parameter=Test&intParamenter=123", true, false);
	}	

	@Expose
	public void onClickScreenWrapperItem(){
		screen.getTabs().openTab(SCREEN_WRAPPER_TAB, "Screen Wrapper", SCREEN_WRAPPER_TAB + HTML, true, false);
	}	

	@Expose
	public void onClickScreenCommunicationItem(){
		screen.getTabs().openTab(SCREEN_COMMUNICATION_TAB, "Screen Communication", SCREEN_COMMUNICATION_TAB + HTML, true, false);
	}	

	@Expose
	public void onClickDynaTabsItem(){
		screen.getTabs().openTab(DYNA_TABS_TAB, "Dyna Tabs", DYNA_TABS_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickConfirmItem(){
		screen.getTabs().openTab(CONFIRM_TAB, "Confirm", CONFIRM_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickMessageBoxItem(){
		screen.getTabs().openTab(MESSAGE_BOX_TAB, "Message Box", MESSAGE_BOX_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickPopupItem(){
		screen.getTabs().openTab(POPUP_TAB, "Popup", POPUP_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickProgressDialogItem(){
		screen.getTabs().openTab(PROGRESS_DIALOG_TAB, "Progress Dialog", PROGRESS_DIALOG_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickValidationItem(){
		screen.getTabs().openTab(VALIDATION_TAB, "Validation", VALIDATION_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickContextItem(){
		screen.getTabs().openTab(CONTEXT_TAB, "Shared Context", CONTEXT_TAB + HTML, true, false);
	}

	@Expose
	public void onClickServerItem(){
		screen.getTabs().openTab(SERVER_COMMUNICATION_TAB, "Server Communication", SERVER_COMMUNICATION_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickI18NItem(){
		screen.getTabs().openTab(I18N_TAB, "I18N", I18N_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickSensitiveMethodProtectionItem(){
		screen.getTabs().openTab(SENSITIVE_METHOD_TAB, "Sensitive Method Protection", SENSITIVE_METHOD_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickSimpleGridItem(){
		screen.getTabs().openTab(SIMPLE_GRID_TAB, "Simple Grid", SIMPLE_GRID_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickStreamingGridItem(){
		screen.getTabs().openTab(STREAMING_GRID_TAB, "Streaming Data Grid", STREAMING_GRID_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickWidgetGridItem(){
		screen.getTabs().openTab(WIDGET_GRID_TAB, "Grid with Widgets", WIDGET_GRID_TAB + HTML, true, false);
	}
	
	@Expose
	public void onClickGridEventsItem(){
		screen.getTabs().openTab(GRID_EVENTS_TAB, "Grid Events", GRID_EVENTS_TAB + HTML, true, false);
	}
	
	protected static interface MainScreen extends ScreenWrapper {
		DynaTabs getTabs();
	}
}