package br.com.sysmap.crux.core.client.event;

public class ValidateException extends Exception 
{
	private static final long serialVersionUID = 5974522064907442546L;

	public ValidateException() 
	{
	}

	public ValidateException(String message) 
	{
		super(message);
	}

	public ValidateException(Throwable cause) 
	{
		super(cause);
	}

	public ValidateException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}