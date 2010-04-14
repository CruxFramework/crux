package br.com.sysmap.crux.showcase.server;

import br.com.sysmap.crux.showcase.client.remote.SensitiveServerService;

public class SensitiveServerServiceImpl implements SensitiveServerService
{
	public String sensitiveMethod()
	{
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			// Nothing
		}
		return "Hello, Sensitive Method called!";
	}

	public String sensitiveMethodNoBlock()
	{
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			// Nothing
		}
		return "Hello, Sensitive Method called (No Block)!";
	}
}
