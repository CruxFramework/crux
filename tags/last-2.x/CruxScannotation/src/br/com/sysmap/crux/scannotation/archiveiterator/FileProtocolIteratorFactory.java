package br.com.sysmap.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileProtocolIteratorFactory implements DirectoryIteratorFactory
{

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
				return new JarIterator(f, filter);
			}
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
