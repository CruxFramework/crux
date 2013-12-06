/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.maskedtextbox;

import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@FormatterName("CruxFilter.Word")
public class WordFilterFormatter extends FilteredTextBoxBaseFormatter
{

	public String getFilter()
	{
		return "[a-zA-Z]";
	}

	public String format(Object input) throws InvalidFormatException
	{
		if (input == null)
		{
			return null;
		}
		return input.toString().replaceAll("^[a-zA-Z]", "");
	}

	public Object unformat(String input) throws InvalidFormatException
	{
		return input;
	}

}
