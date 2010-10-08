package br.com.sysmap.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FileProtocolIteratorFactory implements DirectoryIteratorFactory
{

	/**
	 * @see br.com.sysmap.crux.scannotation.archiveiterator.DirectoryIteratorFactory#create(java.net.URL, br.com.sysmap.crux.scannotation.archiveiterator.Filter)
	 */
	public URLIterator create(URL url, Filter filter) throws IOException
	{
		try
		{
			File f = new File(url.toURI());
			if (f.isDirectory())
			{
				return new FileIterator(f, filter);
			}
			else
			{
				return new JarProtocolIterator(url, filter, null);
			}
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
