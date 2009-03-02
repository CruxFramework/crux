package br.com.sysmap.crux.core.i18n;

/**
 * Abstraction for message creation error.
 * @author Thiago
 *
 */
public class MessageException extends RuntimeException 
{
	private static final long serialVersionUID = 7156094304539900927L;

	public MessageException() 
	{
	}

	public MessageException(String message) 
	{
		super(message);
	}

	public MessageException(Throwable cause) 
	{
		super(cause);
	}

	public MessageException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
