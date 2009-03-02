package br.com.sysmap.crux.core.client.component;

/**
 * Abstraction for interface configuration error.
 * @author Thiago
 *
 */
public class InterfaceConfigException extends Exception 
{

	private static final long serialVersionUID = 6965470165290418198L;

	public InterfaceConfigException() 
	{
	}

	public InterfaceConfigException(String message) 
	{
		super(message);
	}

	public InterfaceConfigException(Throwable cause) 
	{
		super(cause);
	}

	public InterfaceConfigException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
