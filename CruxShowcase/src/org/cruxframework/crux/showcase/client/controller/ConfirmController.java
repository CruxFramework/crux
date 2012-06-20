package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.Confirm;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.CancelHandler;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;

@Controller("confirmController")
public class ConfirmController {
	
	@Expose
	public void showConfirm(){
				
		Confirm.show(
			
			"Hey!",
			
			"Do you think Tony Iommy is better guitarist than Eric Clapton?", 
			
			"Yes, absolutely!",
			
			"No, man! Are you crazy?",
			
			new OkHandler(){
				public void onOk(OkEvent event){
					MessageBox.show("Result", "Man, I agree with you! Paranoid album is on the edge of the perfection!", null);
				}
			},			
			
			new CancelHandler(){
				public void onCancel(CancelEvent event){
					MessageBox.show("Result", "Sure, dude! Clapton is a God! That's all!", null);
				}
			}
		);
	}
}