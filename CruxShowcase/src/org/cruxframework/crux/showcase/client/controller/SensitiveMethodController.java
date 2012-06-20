package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.remote.SensitiveServerServiceAsync;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;

import com.google.gwt.user.client.ui.CheckBox;


@Controller("sensitiveMethodController")
public class SensitiveMethodController {
	
	@Create
	protected SensitiveServerServiceAsync service;
	
	@Expose
	public void onClick()
	{
		boolean block = Screen.get("block", CheckBox.class).getValue();
		
		if(block)
		{
			service.sensitiveMethod(new AsyncCallbackAdapter<String>(this){
				
				@Override
				public void onComplete(String result){
					MessageBox.show("Message", result, null);
				}
			});
		}
		else
		{
			service.sensitiveMethodNoBlock(new AsyncCallbackAdapter<String>(this){
				
				@Override
				public void onComplete(String result){
					MessageBox.show("Message", result, null);
				}
			});
		}
	}
}