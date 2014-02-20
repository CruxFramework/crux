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