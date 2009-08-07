package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.context.Context;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

@Controller("contextInitializerController")
public class ContextInitializerController {

	public static interface SharedContext extends Context {
		String getMessage();
		void setMessage(String message);
		Date getDate();
		void setDate(Date date);
	}
	
	
	/**
	 * Called when the main page is rendered, 
	 * since Screen.createContext() must be called only once.    
	 */
	@Expose
	public void onLoad(){
		Screen.createContext();
	}
}