package br.com.sysmap.crux.module.client.remote;

public class ModuleInfoException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ModuleInfoException()
	{
	}

	public ModuleInfoException(String message)
	{
		super(message);
	}

	public ModuleInfoException(Throwable cause)
	{
		super(cause);
	}

	public ModuleInfoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
