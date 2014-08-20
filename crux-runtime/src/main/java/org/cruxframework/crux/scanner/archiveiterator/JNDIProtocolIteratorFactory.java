package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * 
 * @author Samuel Almeida Cardoso
 */
public class JNDIProtocolIteratorFactory implements DirectoryIteratorFactory
{

	public URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
	{
		try
		{
			String urlPath = url.toString();
			
			return new JNDIProtocolIterator(url, scanners, urlPath);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
