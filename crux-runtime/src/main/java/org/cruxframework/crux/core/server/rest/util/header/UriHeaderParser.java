package org.cruxframework.crux.core.server.rest.util.header;

import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriHeaderParser
{
   public static URI fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("URI value is null");
      return URI.create(value);
   }

   public static String toString(URI uri)
   {
      return uri.toASCIIString();
   }
}