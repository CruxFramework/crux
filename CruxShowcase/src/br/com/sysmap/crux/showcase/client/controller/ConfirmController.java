package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.dialog.Confirm;
import br.com.sysmap.crux.widgets.client.event.dialog.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.dialog.CancelHandler;
import br.com.sysmap.crux.widgets.client.event.dialog.OkEvent;
import br.com.sysmap.crux.widgets.client.event.dialog.OkHandler;

import com.google.gwt.user.client.ui.Label;

@Controller("confirmController")
public class ConfirmController {
	
	@Expose
	public void showConfirm(){
		
		Confirm.show(
		
			"An Important Question", "Is the truth out there?", 
			
			new OkHandler(){
				public void onOk(OkEvent event)
				{
					Screen.get("message", Label.class).setText("Yes, it is!");
				}
			},
			
			new CancelHandler(){
				public void onCancel(CancelEvent event)
				{
					Screen.get("message", Label.class).setText("No, it is not!");
				}
			}
		);
	}
}