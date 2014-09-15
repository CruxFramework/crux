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
package org.cruxframework.crux.core.server.rest.util;

import java.util.Locale;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocaleHelper
{
   public static Locale extractLocale(String lang)
   {
      int q = lang.indexOf(';');
      if (q > -1) lang = lang.substring(0, q);
      String[] split = lang.trim().split("-");
      if (split.length == 1) return new Locale(split[0].toLowerCase());
      else if (split.length == 2) return new Locale(split[0].toLowerCase(), split[1].toLowerCase());
      else if (split.length > 2) return new Locale(split[0], split[1], split[2]);
      return null; // unreachable
   }

   /**
    * HTTP 1.1 has different String format for language than what java.util.Locale does '-' instead of '_'
    * as a separator
    *
    * @param value
    * @return
    */
   public static String toLanguageString(Locale value)
   {
      StringBuffer buf = new StringBuffer(value.getLanguage().toLowerCase());
      if (value.getCountry() != null && !value.getCountry().equals(""))
         buf.append("-").append(value.getCountry().toLowerCase());
      return buf.toString();
   }
}