package br.com.sysmap.crux.showcase.server;

import br.com.sysmap.crux.showcase.client.remote.ServerService;

public class ServerServiceImpl implements ServerService
{
	public String sayHello(String name)
	{
		return "Hello, "+name+"!";
	}

}
