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

import java.util.Locale;

import org.cruxframework.crux.core.server.rest.util.LocaleHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocaleHeaderParser
{
   public static Locale fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("Locale value is null");
      return LocaleHelper.extractLocale(value);
   }

   public static String toString(Locale value)
   {
      return LocaleHelper.toLanguageString(value);
   }

}