package br.com.sysmap.crux.widgets.client.grid;

public class GridException extends RuntimeException
{
	private static final long serialVersionUID = 713792714882951340L;

	public GridException()
	{
		super();
	}

	public GridException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GridException(String message)
	{
		super(message);
	}

	public GridException(Throwable cause)
	{
		super(cause);

	}
}
