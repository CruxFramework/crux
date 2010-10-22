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
package br.com.sysmap.crux.core.client.utils;

import com.google.gwt.core.client.GWT;

/**
 * @author Gesse S. F. Dafe
 * @author Thiago da Rosa de Bustamante -
 */
public class StringUtils
{
	/**
	 * @param src
	 * @param length
	 * @param padding
	 * @return
	 */
	public static String lpad(String src, int length, char padding)
	{
		if(src == null)
		{
			src = "";
		}

		while(src.length() < length)
		{
			src = padding + src;
		}
		
		return src;
	}
	
	/**
	 * @param src
	 * @param length
	 * @param padding
	 * @return
	 */
	public static String rpad(String src, int length, char padding)
	{
		if(src == null)
		{
			src = "";
		}

		while(src.length() < length)
		{
			src += padding;
		}
		
		return src;
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value)
	{
		return (value == null || value.length() == 0);
	}
	
	/**
	 * @param text
	 * @param searchString
	 * @param replacement
	 * @return
	 */
	public static String replace(String text, String searchString, String replacement) 
	{
		return replace(text, searchString, replacement, -1);
	}

	/**
	 * @param text
	 * @param searchString
	 * @param replacement
	 * @param max
	 * @return
	 */
	public static String replace(String text, String searchString, String replacement, int max) 
	{
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) 
		{
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) 
		{
			return text;
		}
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = (increase < 0 ? 0 : increase);
		increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
		StringBuffer buf = new StringBuffer(text.length() + increase);
		while (end != -1) 
		{
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) 
			{
				break;
			}
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}
	
	/**
	 * Transforms "some text" into "Some text"
	 * @param text
	 * @return
	 */
	public static String toUpperCaseFirstChar(String text)
	{
		if(text != null && text.length() > 0)
		{
			return text.substring(0,1).toUpperCase() + text.substring(1);
		}
		
		return text;
	}
	
	/**
	 * This method generates a faster string comparison than {@code String.equals()}
	 * for web mode code.
	 * 
	 * The GWT generated {@code String.equals()} method makes a check in its parameter's type in runtime. 
	 * That method makes that assertion on compilation time (once its parameters are both 
	 * Strings).
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(String a, String b)
	{
		if (GWT.isScript())
		{
			if (a==null) return (b==null);
			return (b!= null && a==b);
		}
		else
		{
			if (a==null) return (b==null);
			return (b!= null && a.equals(b));
		}
	}

	/**
	 * This method generate a faster string comparison than {@code String.equals()}
	 * for web mode code. It is similar to {@code StringUtils.equals()} method.
	 * The only difference is that this one does not check for null parameters.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean unsafeEquals(String a, String b)
	{
		if (GWT.isScript())
		{
			return a==b;
		}
		else
		{
			return a.equals(b);
		}
	}
}
