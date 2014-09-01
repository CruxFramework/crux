package org.cruxframework.crux.scanner.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.scanner.ScannerRegistration;
import org.cruxframework.crux.scanner.Scanners;



/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class ZIPProtocolIterator extends URLIterator
{
	protected ZipInputStream zipStream;
	protected ZipEntry next;
	protected String pathInZip;
	protected boolean initial = true;
	protected boolean closed = false;
	protected URL zip;
	private static final Log LOG = LogFactory.getLog(ZIPProtocolIterator.class);
	
	public ZIPProtocolIterator(URL zip, List<ScannerRegistration> scanners, String pathInZip) throws IOException, URISyntaxException
	{
		super(scanners);
		if (zip.toString().startsWith("file:"))
		{
			this.zip = toZipURL(zip);
			this.zipStream = new ZipInputStream(new FileInputStream(new File(zip.toURI())));
		}
		else
		{
			this.zip = zip;
			this.zipStream = new ZipInputStream(zip.openStream());
		}
		this.pathInZip = (pathInZip==null?"":pathInZip);
		if (this.pathInZip.startsWith("/"))
		{
			this.pathInZip = this.pathInZip.substring(1);
		}
	}
	
	protected ZIPProtocolIterator(List<ScannerRegistration> scanners)
	{
		super(scanners);
	}

	@Override
	public void search()
	{
		URL url = next();
		while (url != null)
		{
			String fileName = getNextEntryFullName();
			if (!Scanners.ignoreScan(zip, fileName))
            {
	            consumeWhenAccepted(zip, url, fileName);
            }
			url = next();
		}
	}
	
	private void setNext()
	{
		initial = true;
		try
		{
			if (next != null) zipStream.closeEntry();
			next = null;
			do
			{
				next = zipStream.getNextEntry();
			} while (next != null && (next.isDirectory() || !next.getName().startsWith(pathInZip)));
			if (next == null)
			{
				close();
			}
		}
		catch (Exception e)
		{
			next = null;
			LOG.error("failed to browse jar", e);
			close();
		}
	}
	
	/**
	 * Returns the full name of the next zip entry.
	 * @return
	 */
	private String getNextEntryFullName()
	{
		String prefix = this.zip.toString();
		String nextEntry = this.next.getName();
		
		if(prefix.endsWith("/"))
		{
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		
		if(nextEntry.startsWith("/"))
		{
			nextEntry = nextEntry.substring(1);
		}
			
		return prefix + "/" + nextEntry;
	}

	protected URL next()
	{
		if (closed || (next == null && !initial)) return null;
		setNext();
		if (next == null) return null;
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(getProtocol());
		return handler.getChildResource(zip, next.getName());
	}

	protected String getProtocol()
	{
		return "zip";
	}

	protected void close()
	{
		try
		{
			closed = true;
			zipStream.close();
		}
		catch (IOException ignored)
		{
			//Do nothing
		}
	}

	private URL toZipURL(URL fileURL) throws MalformedURLException
	{
		return new URL("zip:"+fileURL.toString().substring(5)+"!/");
	}	
}
