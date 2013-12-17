package org.cruxframework.crossdeviceshowcase.client.controller.samples.maskedtextbox;

import java.util.Date;

import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

import com.google.gwt.i18n.client.DateTimeFormat;

@FormatterName("maskedDateFormatter")
public class MaskedDateFormatter extends MaskedTextBoxBaseFormatter
{
	@Override
	public String getMask() 
	{
		return "99/99/9999";
	}
	
	@Override
	public String format(Object input) throws InvalidFormatException
	{
		return DateTimeFormat.getFormat("dd/MM/yyyy").format((Date) input);
	}

	@Override
	public Object unformat(String input) throws InvalidFormatException
	{
		return DateTimeFormat.getFormat("dd/MM/yyyy").parseStrict(input);
	}
}
