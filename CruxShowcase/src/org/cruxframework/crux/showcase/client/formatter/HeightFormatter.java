package org.cruxframework.crux.showcase.client.formatter;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;

import com.google.gwt.i18n.client.NumberFormat;

@FormatterName("height")
public class HeightFormatter implements Formatter {

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#unformat(java.lang.String)
	 */
	public Object unformat(String input) {
		
		if (input == null || input.length() == 0 || !(input.contains("'") && input.contains("\""))) {
			return null;
		}

		input = input.replace("\"", "").replace("'", ".");
		
		return Double.parseDouble(input);
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input) throws InvalidFormatException {
		
		if (input == null) {
			return "";
		}

		if (!(input instanceof Double)) {
			throw new InvalidFormatException();
		}
		
		String text = NumberFormat.getFormat("0.0#").format((Double) input);
		text = text.replace(".", "'");
		text = text + "\"";
		return text;
	}
}