package org.cruxframework.crossdeviceshowcase.client.controller.samples.maskedlabel;

import java.util.Date;

import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Formatter for date fields.
 * 
 * @author Gesse Dafe
 */
@FormatterName("dateFormatter")
public class DateFormatter implements Formatter
{
	@Override
	public String format(Object input) throws InvalidFormatException
	{
		return DateTimeFormat.getFormat("dd/MM/yyyy").format((Date) input);
	}

	@Override
	public Object unformat(String input) throws InvalidFormatException
	{
		return DateTimeFormat.getFormat("dd/MM/yyyy").parse(input);
	}

}
