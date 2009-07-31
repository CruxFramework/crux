package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.advanced.client.maskedtextbox.MaskedTextBox;
import br.com.sysmap.crux.advanced.client.maskedtextbox.MaskedTextBoxBaseFormatter;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.formatter.annotation.FormatterName;

import com.google.gwt.i18n.client.DateTimeFormat;

@Controller("maskedTextBoxController")
public class MaskedTextBoxController {
	private String formatter = "phone";
	
	@Expose
	public void changeFormat(){
		MaskedTextBox maskedTextBox = Screen.get("maskedTextBox", MaskedTextBox.class);
		maskedTextBox.setFormatter(Screen.getFormatter("phone".equals(formatter)?"date":"phone"));
		formatter = "phone".equals(formatter)?"date":"phone";
	}
	
	@FormatterName("phone")
	public static class PhoneFormatter extends MaskedTextBoxBaseFormatter implements Formatter{
		@Override
		protected String getMask(){
			return "(99)9999-9999";
		}

		public String format(Object input){
			if (input == null || !(input instanceof String) || ((String)input).length() != 10){
				return "";
			}
			
			String strInput = (String) input;
			return "("+strInput.substring(0,2)+")"+strInput.substring(2,6)+"-"+strInput.substring(6);
		}

		public Object unformat(String input) throws InvalidFormatException{
			if (input == null || !(input instanceof String) || ((String)input).length() != 13){
				return "";
			}
			String inputStr = (String)input;
			inputStr = inputStr.substring(1,3)+inputStr.substring(4,8)+inputStr.substring(9,13);
			return inputStr;
		}
	}

	@FormatterName("date")
	public static class DateFormatter extends MaskedTextBoxBaseFormatter implements Formatter {
		DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");

		@Override
		protected String getMask(){
			return "99/99/9999";
		}

		public Object unformat(String input){
			if (input == null || input.length() != 10){
				return null;
			}
			
			return format.parse(input);
		}

		public String format(Object input) throws InvalidFormatException {
			if(input == null){
				return "";
			}
			if (!(input instanceof Date)){
				throw new InvalidFormatException();
			}
			
			return format.format((Date) input);
		}
	}	
}