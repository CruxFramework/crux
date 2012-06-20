package org.cruxframework.crux.samples.server;

import org.cruxframework.crux.samples.client.remote.GreetingService;

public class GreetingServiceImpl implements GreetingService
{
	public String getHelloMessage(String name)
	{
		return "Server says: Hello, " + name + "!";
	}
}
