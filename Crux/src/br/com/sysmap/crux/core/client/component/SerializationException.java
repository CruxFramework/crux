package br.com.sysmap.crux.core.client.component;

/**
 * Abstraction for a serialization error.
 * @author Thiago
 *
 */
public class SerializationException extends Exception 
{
	private static final long serialVersionUID = -4216047214555702615L;

	public SerializationException() 
	{
	}

	public SerializationException(String message) 
	{
		super(message);
	}

	public SerializationException(Throwable cause) 
	{
		super(cause);
	}

	public SerializationException(String message, Throwable cause) 
	{
		super(message, cause);
	}

}
