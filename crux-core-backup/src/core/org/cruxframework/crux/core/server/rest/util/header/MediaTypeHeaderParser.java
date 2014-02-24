package org.cruxframework.crux.core.server.rest.util.header;

import java.util.HashMap;

import org.cruxframework.crux.core.server.rest.core.MediaType;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHeaderParser 
{
   public static MediaType fromString(String type) throws IllegalArgumentException
   {
      if (type == null) throw new IllegalArgumentException("MediaType value is null");
      return parse(type);
   }


   public static MediaType parse(String type)
   {
      String params = null;
      int idx = type.indexOf(";");
      if (idx > -1)
      {
         params = type.substring(idx + 1).trim();
         type = type.substring(0, idx);
      }
      String major = null;
      String subtype = null;
      String[] paths = type.split("/");
      if (paths.length < 2 && type.equals("*"))
      {
         major = "*";
         subtype = "*";

      }
      else if (paths.length != 2)
      {
         throw new IllegalArgumentException("Failure parsing MediaType string: " + type);
      }
      else if (paths.length == 2)
      {
         major = paths[0];
         subtype = paths[1];
      }
      if (params != null && !params.equals(""))
      {
         HashMap<String, String> typeParams = new HashMap<String, String>();

         int start = 0;

         while (start < params.length())
         {
            start = HeaderParameterParser.setParam(typeParams, params, start);
         }
         return new MediaType(major, subtype, typeParams);
      }
      else
      {
         return new MediaType(major, subtype);
      }
   }

   public static String toString(MediaType type)
   {
      String rtn = type.getType().toLowerCase() + "/" + type.getSubtype().toLowerCase();
      if (type.getParameters() == null || type.getParameters().size() == 0) return rtn;
      for (String name : type.getParameters().keySet())
      {
         String val = type.getParameters().get(name);
         rtn += ";" + name + "=\"" + val + "\"";
      }
      return rtn;
   }
}