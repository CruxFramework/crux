package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.context.Context;
import br.com.sysmap.crux.core.client.screen.Screen;

public class ContextInitializer{
	
	public static interface SharedContext extends Context {
		String getMessage();
		void setMessage(String message);
		Date getDate();
		void setDate(Date date);
	}

	public static void initialize(){
		Screen.createContext();
	}	
}
