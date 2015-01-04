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
package org.cruxframework.crux.core.server.rest.util.header;

import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParameterParser
{
   static int getEndName(String params, int start)
   {
      int equals = params.indexOf('=', start);
      int semicolon = params.indexOf(';', start);
      if (equals == -1 && semicolon == -1) return params.length();
      if (equals == -1) return semicolon;
      if (semicolon == -1) return equals;
      int end = (equals < semicolon) ? equals : semicolon;
      return end;
   }

   public static int setParam(HashMap<String, String> typeParams, String params, int start)
   {
      boolean quote = false;
      boolean backslash = false;

      int end = getEndName(params, start);
      String name = params.substring(start, end).trim();
      if (end < params.length() && params.charAt(end) == '=') end++;

      StringBuilder buffer = new StringBuilder(params.length() - end);
      int i = end;
      for (; i < params.length(); i++)
      {
         char c = params.charAt(i);

         switch (c)
         {
            case '"':
            {
               if (backslash)
               {
                  backslash = false;
                  buffer.append(c);
               }
               else
               {
                  quote = !quote;
               }
               break;
            }
            case '\\':
            {
               if (backslash)
               {
                  backslash = false;
                  buffer.append(c);
               }
               break;
            }
            case ';':
            {
               if (!quote)
               {
                  String value = buffer.toString().trim();
                  typeParams.put(name, value);
                  return i + 1;
               }
               else
               {
                  buffer.append(c);
               }
               break;
            }
            default:
            {
               buffer.append(c);
               break;
            }
         }
      }
      String value = buffer.toString().trim();
      typeParams.put(name, value);
      return i;
   }
}