package org.cruxframework.crux.showcase.server;

import org.cruxframework.crux.showcase.client.remote.PleaseTypeYourNameException;
import org.cruxframework.crux.showcase.client.remote.ServerCommunicationService;

public class ServerCommunicationServiceImpl implements ServerCommunicationService {
	
	/**
	 * @see org.cruxframework.crux.showcase.client.remote.ServerCommunicationService#sayHello(java.lang.String)
	 */
	public String sayHello(String name) throws PleaseTypeYourNameException{
	
		if(name == null || name.length() == 0){
			throw new PleaseTypeYourNameException("Please enter your name before pressing the button.");
		}
		
		return "Hello, " + name + "! Welcome to Crux!";
	}
}
