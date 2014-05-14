package org.cruxframework.crux.scanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 * Wrapper for handling the opening of URL input streams.
 * Only necessary because of a buggy behavior of {@link JarURLConnection}, 
 * that keeps the lock of the jar file despite of we call <code>urlInputStream.close()</code>.   
 * @author Gesse Dafe
 */
public class URLStreamManager
{
	private final URL url;

	private InputStream inputStream;
	private URLConnection con = null;
	private JarFile jarFile = null;
	
	/**
	 * Creates a stream manager for a given URL.
	 * @param url
	 */
	public URLStreamManager(URL url)
	{
		this.url = url;
	}
	
	/**
	 * Opens the URL's stream
	 * @return
	 * @throws IOException 
	 */
	public InputStream open()
	{
		try
		{
			if ("file".equals(url.getProtocol()))
			{
				if (new File(url.toURI()).exists())
				{
					inputStream = url.openStream();
				}
			}
			else
			{
				con = url.openConnection();
				
				if(con instanceof JarURLConnection)
				{
					JarURLConnection jarCon = (JarURLConnection) con;
					jarCon.setUseCaches(false);
					jarFile = jarCon.getJarFile();
				}
				
				inputStream = con.getInputStream();
			}
		}
		catch (Exception e)
		{
		}
		
		return inputStream;
	}
	
	/**
	 * Reads and stores a resource into memory
	 * @return
	 */
	public static ByteArrayInputStream bufferedRead(URL url)
	{
		try
		{
			URLStreamManager manager = new URLStreamManager(url);
			InputStream stream = manager.open();		
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			int read = 0;
			byte[] buffer = new byte[1204];
			while((read = stream.read(buffer)) > 0)
			{
				out.write(buffer, 0, read);
			}
			manager.close();
			return new ByteArrayInputStream(out.toByteArray());
		}
		catch (Exception e)
		{
			return null;
		}
	}
		
	/**
	 * Closes the underlying input stream
	 * @throws IOException 
	 */
	public void close()
	{
		try
		{
			if(inputStream != null)
			{
				inputStream.close();
			}
			
			if(jarFile != null)
			{
				jarFile.close();
			}
		}
		catch (Exception e)
		{
		}
	}
}
