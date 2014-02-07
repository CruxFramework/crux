package org.cruxframework.cruxdevtools.crudgenerator;

public class GenerationException extends RuntimeException 
{
	private static final long serialVersionUID = -705897360938477997L;

	public GenerationException() 
	{
		super();
	}

	public GenerationException(String msg, Throwable t) 
	{
		super(msg, t);
	}

	public GenerationException(String msg) 
	{
		super(msg);
	}

	public GenerationException(Throwable t) 
	{
		super(t);
	}
}
