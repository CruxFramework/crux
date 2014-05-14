package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ZIPProtocolIteratorFactory implements DirectoryIteratorFactory
{

	public URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
	{
		try
		{
			String pathInJar = null;
			String urlPath = url.toString();
			int firstSlash = urlPath.indexOf("/");
			urlPath = urlPath.substring(firstSlash + 1);
			urlPath = "file:///" + urlPath;
			int jarPathEnd = urlPath.indexOf("!");
			if (jarPathEnd > 0)
			{
				url = new URL(urlPath.substring(0, jarPathEnd));
				pathInJar = urlPath.substring(jarPathEnd+1);
			}
			
			return new ZIPProtocolIterator(url, scanners, pathInJar);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
