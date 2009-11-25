package br.com.sysmap.crux.showcase.client.formatter;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;
import br.com.sysmap.crux.core.client.formatter.annotation.FormatterName;

@FormatterName("birthday")
public class BirthdayFormatter implements Formatter {
	
	public String format(Object input) {
		
		if (input == null || !(input instanceof Date)) {
			return "";
		}
		
		return DateTimeFormat.getFormat("MMM, d").format((Date) input);
	}

	public Object unformat(String input) throws InvalidFormatException {
		
		if (input == null || !(input instanceof String)) {
			return "";
		}
		
		return DateTimeFormat.getFormat("MMM, d").parse(input);
	}
}
