package org.cruxframework.crossdeviceshowcase.client.remote.showcase;

public class ShowcaseException extends Exception
{
	private static final long serialVersionUID = -3373065390028603378L;

	public ShowcaseException()
	{
	}
	
	public ShowcaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ShowcaseException(String message)
	{
		super(message);
	}
	
	public ShowcaseException(Throwable cause)
	{
		super(cause);
	}
}
