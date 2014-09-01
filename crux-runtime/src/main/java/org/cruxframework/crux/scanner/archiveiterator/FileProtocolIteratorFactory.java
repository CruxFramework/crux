package org.cruxframework.crux.scanner.archiveiterator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerException;
import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FileProtocolIteratorFactory implements DirectoryIteratorFactory
{
	@Override
	public URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
	{
		try
		{
			File f = new File(url.toURI());
			if (!f.exists())
			{
				return null;
			}
			if (f.isDirectory())
			{
				return new FileIterator(f, scanners);
			}
			else
			{
				return new JarProtocolIterator(url, scanners, null);
			}
		}
		catch (URISyntaxException e)
		{
			throw new ScannerException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
