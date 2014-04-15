package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.scanner.ScannerRegistration;


/**
 * @author Thiago da Rosa de Bustamante
 */
public class IteratorFactory
{
   public static URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
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
      return factory.create(url, scanners);
   }
}
