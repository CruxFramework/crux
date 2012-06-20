package org.cruxframework.crux.showcase.client.formatter;
import java.util.Date;

import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

import com.google.gwt.i18n.client.DateTimeFormat;

@FormatterName("date")
public class DateFormatter extends MaskedTextBoxBaseFormatter {

	DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");

	/**
	 * @see org.cruxframework.crux.core.client.formatter.MaskedFormatter#getMask()
	 */
	public String getMask() {
		return "99/99/9999";
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#unformat(java.lang.String)
	 */
	public Object unformat(String input) {
		
		if (input == null || input.length() != 10){
			return null;
		}

		return format.parse(input);
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input) throws InvalidFormatException {
		
		if (input == null) {
			return "";
		}

		if (!(input instanceof Date)) {
			throw new InvalidFormatException();
		}

		return format.format((Date) input);
	}
}