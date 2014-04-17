package org.cruxframework.crux.core.server.rest.util.header;


import org.cruxframework.crux.core.server.rest.core.Cookie;
import org.cruxframework.crux.core.server.rest.util.ServerCookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieHeaderParser 
{
   public static Cookie fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("Cookie header value was null");
      try
      {
         int version = 0;
         String domain = null;
         String path = null;
         String cookieName = null;
         String cookieValue = null;

         String parts[] = value.split("[;,]");

         for (String part : parts)
         {
            String nv[] = part.split("=", 2);
            String name = nv.length > 0 ? nv[0].trim() : "";
            String value1 = nv.length > 1 ? nv[1].trim() : "";
            if (value1.startsWith("\"") && value1.endsWith("\"") && value1.length() > 1)
               value1 = value1.substring(1, value1.length() - 1);
            if (!name.startsWith("$"))
            {
               cookieName = name;
               cookieValue = value1;
            }
            else if (name.equalsIgnoreCase("$Version"))
            {
               version = Integer.parseInt(value1);
            }
            else if (name.equalsIgnoreCase("$Path"))
            {
               path = value1;
            }
            else if (name.equalsIgnoreCase("$Domain"))
            {
               domain = value1;
            }
         }
         return new Cookie(cookieName, cookieValue, path, domain, version);
      }
      catch (Exception ex)
      {
         throw new IllegalArgumentException("Failed to parse cookie string '" + value + "'", ex);
      }
   }

   public static String toString(Cookie cookie)
   {
      StringBuffer buf = new StringBuffer();
      ServerCookie.appendCookieValue(buf, 0, cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), null, -1, false);
      return buf.toString();
   }
}