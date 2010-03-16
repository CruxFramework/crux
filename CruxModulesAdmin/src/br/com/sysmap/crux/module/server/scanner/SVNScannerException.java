package br.com.sysmap.crux.module.server.scanner;

public class SVNScannerException extends RuntimeException
{
	private static final long serialVersionUID = 2659016106487660128L;

	public SVNScannerException()
	{ 
	}

	public SVNScannerException(String message)
	{
		super(message);
	}

	public SVNScannerException(Throwable cause)
	{
		super(cause);
	}

	public SVNScannerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
