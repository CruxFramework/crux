package org.cruxframework.crux.gadgets.server;

import org.cruxframework.crux.gadgets.client.remote.GreetingService;

public class GreetingServiceImpl implements GreetingService
{
	public String getHelloMessage(String name)
	{
		return "Server says: Hello, " + name + "!";
	}
}
