package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class JARProtocolIteratorFactory implements DirectoryIteratorFactory
{

	public URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
	{
		try
		{
			String pathInJar = null;
			String urlPath = url.toString().substring(4);
			int jarPathEnd = urlPath.indexOf("!");
			if (jarPathEnd > 0)
			{
				url = new URL(urlPath.substring(0, jarPathEnd));
				pathInJar = urlPath.substring(jarPathEnd+1);
			}
			
			return new JarProtocolIterator(url, scanners, pathInJar);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
