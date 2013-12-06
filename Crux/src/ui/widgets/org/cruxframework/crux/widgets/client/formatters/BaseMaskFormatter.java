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
package org.cruxframework.crux.widgets.client.formatters;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBoxBaseFormatter;

public class BaseMaskFormatter extends MaskedTextBoxBaseFormatter implements Formatter
{
	private static FastMap<String> definitions = new FastMap<String>();
	static
	{
		definitions.put("9", "[0-9]");
		definitions.put("a", "[A-Za-z]");
		definitions.put("*", "[A-Za-z0-9]");
		//any other???
	}
	
	private FastList<String> tests = new FastList<String>();
	private char[] buffer;
	private int length;
	private String mask;
	
	/**
	 * @param mask the current field mask.
	 */
	public BaseMaskFormatter(String mask)
	{
		int firstNonMaskPos = -1;
		
		this.mask = mask;
		this.buffer = new char[mask.length()];
		this.length = mask.length();
		
		for (int i=0; i< mask.length(); i++)
		{
			char c = mask.charAt(i);
			if (c == '?')
			{
				this.length--;
			}
			else
			{
				String key = c+"";
				this.tests.add(definitions.containsKey(key)?definitions.get(key):null);
				if (this.tests.get(this.tests.size()-1) != null && firstNonMaskPos == -1)
				{
					firstNonMaskPos = this.tests.size()-1;
				}
				this.buffer[i] = (definitions.containsKey(key)?getPlaceHolder():c);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input)
	{
		if (!(input instanceof String))
		{
			return "";
		}
		
        char[] charInput = ((String) input).toCharArray();
        
        int i = 0;
        for (int c=0; c<charInput.length; c++)
        {
        	char charValue = charInput[c];
			int p = seekNext(i++);
			if (p < length)
			{
				if (String.valueOf(charValue).matches(tests.get(p)))
				{
					buffer[p] = charValue;
				}
			}
		}	
		return new String(buffer);
	}

	private int seekNext(int pos)
	{
		if (pos < -1)
		{
			pos = -1;
		}
		while (++pos < length)
		{
			if (pos < tests.size() && tests.get(pos) != null && tests.get(pos).length() > 0)
			{
				return pos;
			}
		}
		return length;
	}
	
	public Object unformat(String input) throws InvalidFormatException
	{
		return input;
	}
	
	public String getMask() 
	{
		return mask;
	}
}
