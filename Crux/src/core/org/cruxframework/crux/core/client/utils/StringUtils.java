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
package org.cruxframework.crux.core.client.utils;

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
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static double safeParseDouble(String value)
	{
		if (!isEmpty(value))
		{
			return Double.parseDouble(value);
		}
		return Double.MIN_VALUE;
	}
	
	/**
	 * 
	 * @param multiValuedProperty
	 * @param value
	 * @return
	 */
	public static boolean containsValue(String multiValuedProperty, String value)
	{
		if (isEmpty(value) || isEmpty(multiValuedProperty))
		{
			return false;
		}
		
		String[] parts = multiValuedProperty.split(",");
		for (String part : parts)
        {
	        if (part.trim().equals(value))
	        {
	        	return true;
	        }
        }
		
		return false;
	}
		
	/**
	 *
	 * <p>Find the Levenshtein distance between two Strings.</p>
	 *
	 * <pre>
	 * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
	 * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
	 * StringUtils.getLevenshteinDistance("","")               = 0
	 * StringUtils.getLevenshteinDistance("","a")              = 1
	 * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
	 * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
	 * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
	 * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
	 * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
	 * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
	 * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
	 * </pre>
	 *
	 * @param s  the first String, must not be null
	 * @param t  the second String, must not be null
	 * @return result distance
	 * @throws IllegalArgumentException if either String input <code>null</code>
	 */
	public static int getLevenshteinDistance(String s, String t) 
	{
		if (s == null || t == null) 
		{
			throw new IllegalArgumentException("Strings must not be null");
		}

		int n = s.length(); 
		int m = t.length(); 

		if (n == 0) 
		{
			return m;
		} 
		else if (m == 0) 
		{
			return n;
		}

		if (n > m) 
		{
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n+1]; 
		int d[] = new int[n+1]; 
		int _d[]; 

		int i; 
		int j; 

		char t_j; 

		int cost; 

		for (i = 0; i<=n; i++) 
		{
			p[i] = i;
		}

		for (j = 1; j<=m; j++) 
		{
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) 
			{
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			_d = p;
			p = d;
			d = _d;
		}

		return p[n];
	}
}
