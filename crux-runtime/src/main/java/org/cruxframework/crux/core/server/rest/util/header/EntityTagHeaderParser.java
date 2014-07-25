package org.cruxframework.crux.core.server.rest.util.header;

import org.cruxframework.crux.core.server.rest.core.EntityTag;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EntityTagHeaderParser
{
   public static EntityTag fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("value of EntityTag is null");
      if (value.startsWith("\""))
      {
         value = value.substring(1);
      }
      if (value.endsWith("\""))
      {
         value = value.substring(0, value.length() - 1);
      }
      if (value.startsWith("W/"))
      {
         return new EntityTag(value.substring(2), true);
      }
      return new EntityTag(value);
   }

   public static String toString(EntityTag value)
   {
      String weak = value.isWeak() ? "W/" : "";
      return weak + value.getValue();
   }

}