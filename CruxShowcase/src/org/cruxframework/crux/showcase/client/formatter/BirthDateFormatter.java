package org.cruxframework.crux.showcase.client.formatter;

import java.util.Date;

import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;

import com.google.gwt.i18n.client.DateTimeFormat;

@FormatterName("birthDate")
public class BirthDateFormatter implements Formatter {
	
	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input) {
		
		if (input == null || !(input instanceof Date)) {
			return "";
		}
		
		return DateTimeFormat.getFormat("yyyy, MMMM, d").format((Date) input);
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#unformat(java.lang.String)
	 */
	public Object unformat(String input) throws InvalidFormatException {
		
		if (input == null || !(input instanceof String)) {
			return "";
		}
		
		return DateTimeFormat.getFormat("yyyy, MMMM, d").parse(input);
	}
}
