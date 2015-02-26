/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.colorpicker;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

@FormatterName("hexaColorFormatter")
class HexaColorFormatter extends MaskedTextBoxBaseFormatter implements Formatter
{
	private static FastMap<String> definitions = new FastMap<String>();
	static
	{
		definitions.put("b", "[a-fA-F0-9]");
	}
	
	public HexaColorFormatter()
	{
		super(definitions);
	}

	@Override
	public String getMask()
	{
		return "#bbbbbb";
	}

	public String format(Object input)
	{
		if (input instanceof String)
		{
			input = ((String)input).replaceAll("[a-fA-F0-9]", StringUtils.EMPTY);
		}

		if (!(input instanceof String) || ((String)input).length() != 6)
		{
			return StringUtils.EMPTY;
		}

		return "#" + input;
	}

	public Object unformat(String input) throws InvalidFormatException
	{
		input = (input == null ? StringUtils.EMPTY : input.replaceAll("[a-fA-F0-9]", StringUtils.EMPTY));
		return (input.length() != 6 ? StringUtils.EMPTY : input);
	}
}
