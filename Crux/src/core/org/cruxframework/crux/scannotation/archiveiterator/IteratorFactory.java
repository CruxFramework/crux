package org.cruxframework.crux.scannotation.archiveiterator;

import java.io.IOException;
import java.net.URL;

import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IteratorFactory
{
   public static URLIterator create(URL url, Filter filter) throws IOException
   {
      String urlString = url.toString();
      if (urlString.endsWith("!/"))
      {
         urlString = urlString.substring(4);
         urlString = urlString.substring(0, urlString.length() - 2);
         url = new URL(urlString);
      }

      DirectoryIteratorFactory factory = URLResourceHandlersRegistry.getURLResourceHandler(url.getProtocol()).getDirectoryIteratorFactory();
      if (factory == null) throw new IOException("Unable to scan directory of protocol: " + url.getProtocol());
      return factory.create(url, filter);
   }
}
