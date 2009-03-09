package br.com.sysmap.crux.core.rebind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
	private File file = null;
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
	 * Inform the name of the last page client requested. This is used
	 * only in hosted mode of GWT, when we will have only the developer
	 * working on a page.
	 * @throws IOException 
	 */
	public void registerLastPageRequested(String lastPage) throws IOException
	{
		file = File.createTempFile("bridgeFile", null);
		file.deleteOnExit();
		PrintWriter writer = new PrintWriter(file);
		writer.println(lastPage);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Return the last page requested by client.
	 * @return
	 * @throws IOException 
	 */
	public String getLastPageRequested() throws IOException
	{
		if (file != null)
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String lastPage = reader.readLine();
			reader.close();
			return lastPage;
		}
		else
		{
			return null;
		}
	}
	
}
