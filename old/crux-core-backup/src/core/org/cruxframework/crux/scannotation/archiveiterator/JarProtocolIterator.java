package org.cruxframework.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarInputStream;


/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class JarProtocolIterator extends ZIPProtocolIterator
{
	public JarProtocolIterator(URL zip, Filter filter, String pathInZip) throws IOException, URISyntaxException
	{
		if (zip.toString().startsWith("file:"))
		{
			this.zip = toJarURL(zip);
			this.zipStream = new JarInputStream(new FileInputStream(new File(zip.toURI())));
		}
		else
		{
			this.zip = zip;
			this.zipStream = new JarInputStream(zip.openStream());
		}
		this.filter = filter;
		this.pathInZip = (pathInZip==null?"":pathInZip);
		if (this.pathInZip.startsWith("/"))
		{
			this.pathInZip = this.pathInZip.substring(1);
		}
	}
	
	@Override
	protected String getProtocol()
	{
		return "jar";
	}
	
	private URL toJarURL(URL fileURL) throws MalformedURLException
	{
		return new URL("jar:"+fileURL.toString()+"!/");
	}	
}
