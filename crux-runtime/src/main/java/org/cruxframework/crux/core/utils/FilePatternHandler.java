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
package org.cruxframework.crux.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FilePatternHandler
{
	private static final String PATTERN_SPECIAL_CHARACTERS = "\\{}[]()+?$&^-|.!";
	private String excludes;
	private List<Pattern> excludesPatterns = new ArrayList<Pattern>();
	private String includes;
	private List<Pattern> includesPatterns = new ArrayList<Pattern>();

	
	/**
	 * @param includes
	 * @param excludes
	 */
	public FilePatternHandler(String includes, String excludes)
    {
		processIncludesPatterns(includes);
		processExcludesPatterns(excludes);
    }
	
	/**
	 * @param includes
	 * @param excludes
	 */
	public FilePatternHandler(String[] includes, String[] excludes)
    {
		processIncludesPatterns(includes);
		processExcludesPatterns(excludes);
    }
	
	/**
	 * @return
	 */
	public String getExcludes()
    {
    	return excludes;
    }

	/**
	 * @return
	 */
	public String getIncludes()
    {
    	return includes;
    }

	/**
	 * @param entryName
	 * @return
	 */
	public boolean isValidEntry(String entryName)
    {
		boolean isValid = includesPatterns.size() == 0;
	    for (Pattern pattern : includesPatterns)
        {
	        if (pattern.matcher(entryName).matches())
	        {
	        	isValid = true;
	        	break;
	        }
        }
		
	    for (Pattern pattern : excludesPatterns)
        {
	        if (pattern.matcher(entryName).matches())
	        {
	        	isValid = false;
	        	break;
	        }
        }
	    return isValid;
    }

	/**
	 * @param pattern
	 * @param i
	 * @return
	 */
	private int isDirectoryNavigation(String pattern, int i)
	{
		if (pattern.charAt(i) == '*')
        {
			int length = pattern.length();
			int rest = length - i; 

			if (rest > 2)
			{
				String match = "**/";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
			}			
			if (rest > 1)
			{
				String match = "**";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
			}			
        }		
		return 0;
	}
	
	/**
	 * @param patternChar
	 * @return
	 */
	private boolean needsPatternScape(char patternChar)
    {
		return PATTERN_SPECIAL_CHARACTERS.indexOf(patternChar) >= 0;
    }

	
	/**
	 * @param excludes
	 */
	private void processExcludesPatterns(String excludes)
    {
	    if (excludes != null && excludes.length() > 0)
		{
			String[] excludesPatterns = excludes.split(",");
			for (String excludePattern : excludesPatterns)
            {
	            this.excludesPatterns.add(Pattern.compile(translatePattern(excludePattern), Pattern.CASE_INSENSITIVE));
            }
		}
	    this.excludes = excludes;
    }
	
	/**
	 * @param excludes
	 */
	private void processExcludesPatterns(String[] excludes)
    {
	    if (excludes != null)
		{
	    	StringBuilder sb = new StringBuilder();
	    	String delim = "";
			for (String excludePattern : excludes)
            {
	            this.excludesPatterns.add(Pattern.compile(translatePattern(excludePattern), Pattern.CASE_INSENSITIVE));
	            sb.append(delim).append(excludePattern);
	            delim = ",";
            }
			this.excludes = sb.toString();
		}
    }

	/**
	 * @param includes
	 */
	private void processIncludesPatterns(String includes)
    {
	    if (includes != null && includes.length() > 0)
		{
			String[] includesPatterns = includes.split(",");
			for (String includePattern : includesPatterns)
            {
	            this.includesPatterns.add(Pattern.compile(translatePattern(includePattern), Pattern.CASE_INSENSITIVE));
            }
		}
		this.includes = includes;
    }	
	
	/**
	 * @param includes
	 */
	private void processIncludesPatterns(String[] includes)
    {
	    if (includes != null)
		{
	    	StringBuilder sb = new StringBuilder();
	    	String delim = "";
			for (String includePattern : includes)
            {
	            this.includesPatterns.add(Pattern.compile(translatePattern(includePattern), Pattern.CASE_INSENSITIVE));
	            sb.append(delim).append(includePattern);
	            delim = ",";
            }
			this.includes = sb.toString();
		}
    }	

	/**
	 * @param pattern
	 * @return
	 */
	private String translatePattern(String pattern)
    {
		pattern = pattern.replace("\\", "/").trim();
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i < pattern.length(); i++)
        {
			int increment = isDirectoryNavigation(pattern, i);
			if (increment > 0)
	        {
				i += increment;
        		str.append(".*");
	        }
			else
			{
				if (pattern.charAt(i) == '*')
				{
	        		str.append("[^/]*");
				}
				else
				{
					if (needsPatternScape(pattern.charAt(i)))
					{
						str.append("\\");
					}
					str.append(pattern.charAt(i));
				}
			}
        }
		
	    return str.toString();
    }
}
