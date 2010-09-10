package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.widgets.client.dialog.ProgressDialog;

import com.google.gwt.user.client.Timer;

@Controller("progressDialogController")
public class ProgressDialogController {

	private Timer timer = new Timer(){
		@Override
		public void run()
		{
			ProgressDialog.hide();
		}
	};
	
	@Expose
	public void showProgress(){
		ProgressDialog.show("Example of progress dialog...");
		timer.schedule(3000);		
	}
}