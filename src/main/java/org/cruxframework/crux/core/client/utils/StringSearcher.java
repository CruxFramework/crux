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
package org.cruxframework.crux.core.client.utils;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StringUtils.StringFilter;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A helper class to search substrings inside a bigger string. It uses regular expressions 
 * for searching.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class StringSearcher
{
	private RegExp regExp;

	/**
	 * Create a searcher for the given pattern
	 * @param pattern substring to search
	 */
	public StringSearcher(String pattern)
    {
		this(pattern, false, false);
    }
	
	/**
	 * Create a searcher for the given pattern
	 * @param pattern substring to search
	 * @param acceptWildcard If true, the pattern can contains wildcards.
	 * @param caseSensitive If true, the searches will be case sensitive
	 */
	public StringSearcher(String pattern, boolean acceptWildcard, boolean caseSensitive)
	{
		if (acceptWildcard)
		{
			String[] parts = pattern.split("\\*");
			if (parts.length > 1)
			{
				pattern = StringUtils.join(parts, ".*", new StringFilter()
				{
					@Override
					public String filter(String input)
					{
						return escapeRegExp(input);
					}
				});
			}
			else
			{
				pattern = escapeRegExp(pattern);
			}
		}
		else
		{
			pattern = escapeRegExp(pattern);
		}

		if (caseSensitive)
		{
			regExp = RegExp.compile(pattern);
		}
		else
		{
			regExp = RegExp.compile(pattern, "i");
		}
	}
	
	/**
	 * Searches the given string and returns the first occurrence of pattern on it. 
	 * @param toSearch string to search.
	 * @return the index of first occurrence or -1 if pattern is not present.
	 */
	public int searchOnString(String toSearch)
	{
		MatchResult result = regExp.exec(toSearch);
		if (result != null)
		{
			return result.getIndex();
		}
		return -1;
	}

	private native String escapeRegExp(String string)/*-{
	  return string.replace(/([.*+?^${}()|\[\]\/\\])/g, "\\$1");
	}-*/;
}
