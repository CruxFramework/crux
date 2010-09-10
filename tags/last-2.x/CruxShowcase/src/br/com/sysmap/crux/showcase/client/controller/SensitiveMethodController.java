package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;
import br.com.sysmap.crux.showcase.client.remote.SensitiveServerServiceAsync;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;

@Controller("sensitiveMethodController")
public class SensitiveMethodController {
	
	@Create
	protected SensitiveServerServiceAsync service;
	
	@Expose
	public void onClick()
	{
		service.sensitiveMethod(new AsyncCallbackAdapter<String>(this){
			@Override
			public void onComplete(String result){
				MessageBox.show("Sensitive Method", result, null);
			}
		});
	}

	@Expose
	public void onClickNoBlock()
	{
		service.sensitiveMethodNoBlock(new AsyncCallbackAdapter<String>(this){
			@Override
			public void onComplete(String result){
				MessageBox.show("Sensitive Method", result, null);
			}
		});
	}
}