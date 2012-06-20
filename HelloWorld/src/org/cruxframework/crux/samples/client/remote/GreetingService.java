package org.cruxframework.crux.samples.client.remote;

import com.google.gwt.user.client.rpc.RemoteService;

public interface GreetingService extends RemoteService
{
	public String getHelloMessage(String name);
}
