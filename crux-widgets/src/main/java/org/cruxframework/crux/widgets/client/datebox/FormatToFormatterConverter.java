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
package org.cruxframework.crux.widgets.client.datebox;

import java.util.Date;

import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.datebox.DateBox.CruxFormat;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

import com.google.gwt.i18n.client.DateTimeFormat;

public class FormatToFormatterConverter extends MaskedTextBoxBaseFormatter implements IFormatToFormatterConverter
{
	private DateTimeFormat boxFormat = null;
	private CruxFormat format;
	
	public CruxFormat getFormat() 
	{
		return format;
	}

	public void setFormat(CruxFormat format) 
	{
		this.format = format;
		this.boxFormat = DateTimeFormat.getFormat(format.getPattern());
	}

	@Override
	public String format(Object input) throws InvalidFormatException
	{
		if(boxFormat == null || input == null || (input instanceof String && StringUtils.isEmpty((String)input)))
		{
			return null;
		}
		
		return boxFormat.format((Date) input);
	}
	
	@Override
	public Object unformat(String input) throws InvalidFormatException
	{
		if(boxFormat == null || input == null || (input instanceof String && StringUtils.isEmpty((String)input)))
		{
			return null;
		}
		
		Date parsedValue = null;
		try
		{
			parsedValue = boxFormat.parse(input);	
		} catch(IllegalArgumentException e)
		{
			return null;
		}
		
		return parsedValue;
	}

	@Override
	public String getMask() 
	{
		return format.getPattern()
				.replaceAll(" ", "/")
				.replaceAll("d", "9")
				.replaceAll("D", "9")
				.replaceAll("m", "9")
				.replaceAll("M", "9")
				.replaceAll("y", "9")
				.replaceAll("Y", "9")
				.replaceAll("H", "9")
				.replaceAll("h", "9")
				.replaceAll("s", "9");
	}
}
