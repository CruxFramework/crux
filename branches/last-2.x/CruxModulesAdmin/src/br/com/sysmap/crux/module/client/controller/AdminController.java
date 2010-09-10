package br.com.sysmap.crux.module.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.module.client.AdminMessages;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;
import br.com.sysmap.crux.widgets.client.dialog.Popup;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.widgets.client.stackmenu.StackMenu;
import br.com.sysmap.crux.widgets.client.stackmenu.StackMenuItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("cruxAdminController")
public class AdminController {
	
	@Create
	protected ModuleInfoServiceAsync moduleService;
	
	@Create
	protected AdminScreen screen;
	
	@Create
	protected AdminMessages messages;

	/**
	 * @param event
	 */
	@Expose
	public void onLoadModules(ScreenLoadEvent event)
	{
		moduleService.getModuleNames(new AsyncCallbackAdapter<String[]>(this){
			@Override
			public void onComplete(String[] result)
			{
				populateMenu(result);
			}
		});
	}
	
	/**
	 * 
	 */
	@Expose
	public void onConfigClick()
	{
		Popup.show(messages.configPopupTitle(), "config.html", null);
	}
	
	/**
	 * @param result
	 */
	protected void populateMenu(String[] result)
	{
		if (result != null)
		{
			StackMenu modules = screen.getModules();
			for (final String module : result)
			{
				StackMenuItem item = new StackMenuItem(module);
				item.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent arg0)
					{
						showModuleInfo(module);
					}
				});
				modules.add(item);
			}
		}
	}

	/**
	 * @param module
	 */
	protected void showModuleInfo(String module)
	{
		screen.getModulesTabs().openTab(module, module, "moduleInfo.html?module="+URL.encode(module), true, false);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface AdminScreen extends ScreenWrapper
	{
		StackMenu getModules();
		DynaTabs getModulesTabs();
	}
}