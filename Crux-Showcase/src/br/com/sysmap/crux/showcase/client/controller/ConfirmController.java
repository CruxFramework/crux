package br.com.sysmap.crux.showcase.client.controller;

import com.google.gwt.user.client.ui.Label;

import br.com.sysmap.crux.advanced.client.dialog.Confirm;
import br.com.sysmap.crux.advanced.client.event.dialog.CancelEvent;
import br.com.sysmap.crux.advanced.client.event.dialog.CancelHandler;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvent;
import br.com.sysmap.crux.advanced.client.event.dialog.OkHandler;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

@Controller("confirmController")
public class ConfirmController {
	
	@Expose
	public void showConfirm(){
		
		Confirm.show(
		
			"This is a cross-frame modal confirm", "Is the truth out there?", 
			
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