package org.cruxframework.crux.showcase.server;

import org.cruxframework.crux.showcase.client.remote.SensitiveServerService;

public class SensitiveServerServiceImpl implements SensitiveServerService {
	
	/**
	 * Here we put the thread to sleep to simulate a slow server processing.
	 */
	public String sensitiveMethod() {
		
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e){
			// Nothing
		}
		
		return "The interface-blocking sensitive method has finished. You can now call it again.";
	}

	/**
	 * Here we put the thread to sleep to simulate a slow server processing.
	 */
	public String sensitiveMethodNoBlock() {
		
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e){
			// Nothing
		}
		
		return "The non-blocking sensitive method has finished. You can now call it again.";
	}
}
