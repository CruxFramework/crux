package br.com.sysmap.crux.core.server.lifecycle;

public class PhaseException extends Exception 
{
	private static final long serialVersionUID = 8075634446562753279L;

	public PhaseException() 
	{
	}

	public PhaseException(String message) 
	{
		super(message);
	}

	public PhaseException(Throwable cause) 
	{
		super(cause);
	}

	public PhaseException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
