/*
 * Copyright 2014 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
