package br.com.sysmap.crux.core.client.formatter;

/**
 * 
 * @author Thiago
 */
public class InvalidFormatException extends RuntimeException 
{
	private static final long serialVersionUID = -8583878317698972266L;

	public InvalidFormatException() 
	{
	}

	public InvalidFormatException(String message) 
	{
		super(message);
	}

	public InvalidFormatException(Throwable cause) 
	{
		super(cause);
	}

	public InvalidFormatException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
