package br.com.sysmap.crux.core.server.scan;

public class ComponentConfigScannerException extends RuntimeException 
{
	private static final long serialVersionUID = 6810738683779587271L;

	public ComponentConfigScannerException() 
	{
	}

	public ComponentConfigScannerException(String message) 
	{
		super(message);
	}

	public ComponentConfigScannerException(Throwable cause) 
	{
		super(cause);
	}

	public ComponentConfigScannerException(String message, Throwable cause) 
	{
		super(message, cause);
	}

}
