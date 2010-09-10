package br.com.sysmap.crux.module.client.controller;

import java.util.List;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ControllerName;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.module.client.AdminMessages;
import br.com.sysmap.crux.module.client.remote.LoginServiceAsync;
import br.com.sysmap.crux.widgets.client.dialog.Popup;
import br.com.sysmap.crux.widgets.client.dialog.PopupOpenerInvoker;
import br.com.sysmap.crux.widgets.client.dialog.ProgressDialog;
import br.com.sysmap.crux.widgets.client.grid.DataRow;
import br.com.sysmap.crux.widgets.client.grid.Grid;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("cruxAdminLoginController")
public class LoginController
{
	@Create
	protected LoginScreen screen;
	
	@Create
	protected LoginServiceAsync loginService;
	
	@Create
	protected AdminMessages messages;
	
	@Create
	protected ModuleInfoInvoker moduleInfoInvoker; 
	
	@Expose
	public void authenticate()
	{
		List<DataRow> rows = screen.getLoginGrid().getCurrentPageRows();
		if (rows != null && rows.size() > 0)
		{
			String[] repositories = new String[rows.size()]; 
			String[] users = new String[rows.size()]; 
			String[] passwords = new String[rows.size()]; 

			for (int i=0; i< rows.size(); i++)
			{
				DataRow dataRow = rows.get(i);
				repositories[i] = (String) dataRow.getValue("url");
				users[i] = ((TextBox)dataRow.getWidget("user")).getValue();
				passwords[i] = ((PasswordTextBox)dataRow.getWidget("password")).getValue();
			}
			
			ProgressDialog.show(messages.loginAuthenticating());
			loginService.authenticate(repositories, users, passwords, 
					new AsyncCallbackAdapter<Void>(this){
						@Override
						public void onComplete(Void result)
						{
							moduleInfoInvoker.confirmLogin();
							ProgressDialog.hide();
							Popup.close();
						}
						@Override
						public void onError(Throwable e)
						{
							ProgressDialog.hide();
							super.onError(e);
						}
					});
		}
	}
	
	public static interface LoginScreen extends ScreenWrapper
	{
		Grid getLoginGrid();
	}
	
	@ControllerName("cruxModuleInfoController")
	public static interface ModuleInfoInvoker extends PopupOpenerInvoker
	{
		void confirmLogin();
	}
}
