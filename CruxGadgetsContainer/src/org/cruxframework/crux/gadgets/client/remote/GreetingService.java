package org.cruxframework.crux.gadgets.client.remote;

import com.google.gwt.user.client.rpc.RemoteService;

public interface GreetingService extends RemoteService
{
	public String getHelloMessage(String name);
}
