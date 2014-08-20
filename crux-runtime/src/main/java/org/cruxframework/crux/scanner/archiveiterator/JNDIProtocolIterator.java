package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.classpath.JNDIURLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * @author Samuel Almeida Cardoso 
 *
 */
public class JNDIProtocolIterator extends URLIterator
{
	protected URL jndi;
	protected URL next;
	
	public JNDIProtocolIterator(URL jndi, List<ScannerRegistration> scanners, String pathInZip) throws IOException, URISyntaxException
	{
		super(scanners);
		this.jndi = jndi;
	}
	
	protected JNDIProtocolIterator(List<ScannerRegistration> scanners)
	{
		super(scanners);
	}

	@Override
	public void search()
	{
		// TODO Implement it.
		
		//Sample 1
		
//		try {
//			InitialContext.doLookup(jndi.getPath());
//		} catch (NamingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//Sample 2
		
//		ServletContext context = InitializerListener.getContext();
//	    if (context != null)
//	    {
//	    	
//	    }
		
		//Sample 3
		
//		URL url = next();
//		while (url != null)
//		{
//			String fileName = getNextEntryFullName();
//			if (!Scanners.ignoreScan(jndi, fileName))
//            {
//	            consumeWhenAccepted(jndi, url, fileName);
//            }
//			url = next();
//		}
	}
	
	protected URL next()
	{
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(JNDIURLResourceHandler.PROTOCOL);
		return handler.getChildResource(jndi, next.getPath());
	}
}
