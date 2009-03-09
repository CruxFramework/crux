package br.com.sysmap.crux.core.rebind;


/**
 * A Bridge class for allow Generators to know the name of the module 
 * that starts the generation process. Crux Generators need this information
 * to obtain better performance for method handlers in client side of 
 * applications.
 * 
 * @author Administrator
 *
 */
public class CruxScreenBridge 
{
	private static CruxScreenBridge instance = new CruxScreenBridge();
	public String lastPage = null;
	private CruxScreenBridge() 
	{
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static CruxScreenBridge getInstance()
	{
		return instance;
	}
	
	/** 
	 * Inform the name of the last page the client requested. This is used
	 * only in hosted mode of GWT, when we will have only the developer
	 * working on a page.
	 */
	public void registerLastPageRequested(String lastPage)
	{
		this.lastPage = lastPage;
	}
	
	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getLastPageRequested() 
	{
		return this.lastPage;
	}
	
}
