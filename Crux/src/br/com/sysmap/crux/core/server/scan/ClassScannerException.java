package br.com.sysmap.crux.core.server.scan;

public class ClassScannerException extends RuntimeException 
{
	private static final long serialVersionUID = 6810738683779587271L;

	public ClassScannerException() 
	{
	}

	public ClassScannerException(String message) 
	{
		super(message);
	}

	public ClassScannerException(Throwable cause) 
	{
		super(cause);
	}

	public ClassScannerException(String message, Throwable cause) 
	{
		super(message, cause);
	}

}
