package br.com.sysmap.crux.module.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ControllerName;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Invoker;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.module.client.AdminMessages;
import br.com.sysmap.crux.module.client.dto.ModuleInfo;
import br.com.sysmap.crux.module.client.dto.ModuleInformation;
import br.com.sysmap.crux.module.client.dto.ModuleRef;
import br.com.sysmap.crux.module.client.dto.Page;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;
import br.com.sysmap.crux.widgets.client.dialog.Popup;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.grid.Grid;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

@Controller("cruxModuleInfoController")
public class ModuleInfoController 
{
	@Create
	protected ModuleInfoServiceAsync moduleService;

	@Create
	protected ModuleInfo moduleInfo;

	@Create
	protected AdminMessages messages;
	
	@Create
	protected ModuleInfoScreen screen;
	
	@Create
	protected ModuleInfoParameters parameters;

	private boolean pagesLoaded = false;
	
	private boolean controllersLoaded = false;

	private boolean userLogged = false;

	private boolean depententModulesLoaded = false;

	private boolean datasourcesLoaded = false;

	private boolean formattersLoaded = false;

	private boolean serializablesLoaded = false;
	

	@Expose
	public void loadModuleInfo()
	{
		Screen.blockToUser();
		moduleService.getModuleInfo(parameters.getModuleName(), new AsyncCallbackAdapter<ModuleInformation>(this){
			@Override
			public void onComplete(ModuleInformation result)
			{
				if (result != null)
				{
					moduleInfo = result.getModuleInfo();
					userLogged = result.isAuthenticated();
					screen.getTabContainer().getTabBar().setTabEnabled(1, result.isHasRepositories());
				}
				Screen.unblockToUser();
				Screen.updateScreen(ModuleInfoController.this);
				screen.getRequiredModules().loadData();
			}
			
			@Override
			public void onError(Throwable e)
			{
				super.onError(e);
				Screen.unblockToUser();
			}
		});
	}
	
	@Expose
	public void renderRequiredModulesRows(RowRenderEvent event)
	{
		Image image = (Image) event.getRow().getWidget("statusVersion");
		ModuleRef moduleRef = (ModuleRef) event.getRow().getBoundObject();
		
		if (moduleRef.isStatusVersion() != null && moduleRef.isStatusVersion())
		{
			image.setUrl("images/success.gif");
		}
		else
		{
			image.setUrl("images/fail.gif");
		}
	}
	
	@Expose
	public void onPagesClick()
	{
		if (!this.pagesLoaded)
		{
			screen.getPages().loadData();
			this.pagesLoaded = true;
		}
	}
	
	@Expose
	public void onPagesRowClick(RowDoubleClickEvent event)
	{
		Page page = (Page) event.getRow().getBoundObject();
		
		Popup.show(messages.parametersPopupTitle()+page.getName(), "pageParameters.html?module="+URL.encode(parameters.getModuleName())+"&page="
				                                           +URL.encode(page.getName()), null);
	}
	
	@Expose
	public void onControllersClick()
	{
		if (!this.controllersLoaded)
		{
			screen.getControllers().loadData();
			this.controllersLoaded = true;
		}
	}
	
	@Expose
	public void onDataSourcesClick()
	{
		if (!this.datasourcesLoaded)
		{
			screen.getDatasources().loadData();
			this.datasourcesLoaded = true;
		}
	}
	
	@Expose
	public void onFormattersClick()
	{
		if (!this.formattersLoaded)
		{
			screen.getFormatters().loadData();
			this.formattersLoaded  = true;
		}
	}
	
	@Expose
	public void onSerializablesClick()
	{
		if (!this.serializablesLoaded)
		{
			screen.getSerializables().loadData();
			this.serializablesLoaded  = true;
		}
	}
	
	@Expose
	public void onDependentModulesClick()
	{
		if (!this.depententModulesLoaded)
		{
			if (this.userLogged)
			{
				loadDependentModules();
			}
			else
			{
				Popup.show(messages.loginDescription(), "login.html", "800", "420", null, Popup.DEFAULT_STYLE_NAME, false, true); 
			}
		}
	}

	/**
	 * 
	 */
	private void loadDependentModules()
	{
		moduleService.getDependentModules(parameters.getModuleName(), new AsyncCallbackAdapter<ModuleInfo[]>(this){
			@Override
			public void onComplete(ModuleInfo[] result)
			{
				if (result != null)
				{
					for (ModuleInfo moduleInfo : result)
					{
						screen.getDependentModules().addItem(createDependentModuleTreeItem(moduleInfo));
					}
				}
			}
			
			@Override
			public void onError(Throwable e)
			{
				depententModulesLoaded = false;
			}
		});
		
		this.depententModulesLoaded = true;
	}
	
	protected TreeItem createDependentModuleTreeItem(ModuleInfo moduleInfo)
	{
		TreeItem item = new TreeItem();
		item.setText(moduleInfo.getName()+" - "+ moduleInfo.getVersion());
		item.setTitle(moduleInfo.getDescription());
		item.setUserObject(new Object[]{Boolean.FALSE, moduleInfo});
		item.addItem(" ");
		return item;
	}

	@Expose
	public void onDependentModuleOpen(OpenEvent<TreeItem> event)
	{
		final TreeItem item = event.getTarget();
		
		final Object[] userObject = (Object[]) item.getUserObject();
		Boolean loaded = (Boolean)(userObject)[0];
		if (!loaded)
		{
			ModuleInfo moduleInfo = (ModuleInfo)(userObject)[1];
			item.removeItems();
			moduleService.getDependentModules(moduleInfo.getName(), moduleInfo.getVersion(), new AsyncCallbackAdapter<ModuleInfo[]>(this){
				@Override
				public void onComplete(ModuleInfo[] result)
				{
					userObject[0] = Boolean.TRUE;
					if (result != null)
					{
						for (ModuleInfo info : result)
						{
							item.addItem(createDependentModuleTreeItem(info));
						}
					}
				}
			});
		}
	}
	
	@ExposeOutOfModule
	public void confirmLogin()
	{
		this.userLogged = true;
		loadDependentModules();
	}
	
	@Expose
	public ModuleInfo getModuleInfo()
	{
		return moduleInfo;
	}

	@Expose
	public String getModuleName()
	{
		return parameters.getModuleName();
	}

	public static interface ModuleInfoScreen extends ScreenWrapper
	{
		Grid getRequiredModules();
		Grid getPages();
		Grid getControllers();
		Grid getDatasources();
		Grid getFormatters();
		Grid getSerializables();
		Tree getDependentModules();
		TabPanel getTabContainer();
	}
	
	@ControllerName("cruxModuleInfoController")
	public static interface ModuleInfoControllerInvoker extends Invoker
	{
		ModuleInfo getModuleInfoOnSelf();
	}
}