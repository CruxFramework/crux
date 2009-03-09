/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.utils;

public class HtmlUtils 
{
	public static String filterValue(Object val)
	{
		if(val == null) return "";        
		String value = val.toString();        
		if (value.length() == 0) return "";

		StringBuilder result = null;
		String filtered = null;
		for (int i = 0; i < value.length(); i++)
		{
			filtered = null;
			switch (value.charAt(i))
			{
			case '<':
				filtered = "&lt;";
				break;
			case '>':
				filtered = "&gt;";
				break;
			case '&':
				filtered = "&amp;";
				break;
			case '"':
				filtered = "&quot;";
				break;
			case '\'':
				filtered = "&#39;";
				break;
			}

			if (result == null)
			{
				if (filtered != null)
				{
					result = new StringBuilder(value.length() + 50);
					if (i > 0)
					{
						result.append(value.substring(0, i));
					}
					result.append(filtered);
				}
			}
			else
			{
				if (filtered == null)
				{
					result.append(value.charAt(i));
				}
				else
				{
					result.append(filtered);
				}
			}
		}

		return result == null ? value : result.toString();
	}
}
