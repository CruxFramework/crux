package br.com.sysmap.crux.core.server.screen;

public class ScreenConfigException extends Exception 
{
	private static final long serialVersionUID = -7618543428113242207L;

	public ScreenConfigException() 
	{
	}

	public ScreenConfigException(String message) 
	{
		super(message);
	}

	public ScreenConfigException(Throwable cause) 
	{
		super(cause);
	}

	public ScreenConfigException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
