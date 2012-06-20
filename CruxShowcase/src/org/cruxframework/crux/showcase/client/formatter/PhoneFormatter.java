package org.cruxframework.crux.showcase.client.formatter;

import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

@FormatterName("phone")
public class PhoneFormatter extends MaskedTextBoxBaseFormatter{
	
	public String getMask(){
		return "(999)999-9999";
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input){
		
		String str = "";

		if (input != null){
				
			if(input instanceof Long && ((Long)input) > 999999999L && ((Long)input) < 10000000000L){
				str = input.toString();
				str = "(" + str.substring(0,3) + ")" + str.substring(3,6) + "-" + str.substring(6);
			}
		}
		
		return str;
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#unformat(java.lang.String)
	 */
	public Object unformat(String input) throws InvalidFormatException{
		
		if (input == null || !(input instanceof String) || ((String)input).length() != 13){
			return null;
		}
		
		String inputStr = (String)input;
		inputStr = inputStr.substring(1,4) + inputStr.substring(5,8) + inputStr.substring(9,13);
		return Long.parseLong(inputStr);
	}
}
