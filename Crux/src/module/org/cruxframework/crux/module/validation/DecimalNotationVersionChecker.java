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
package org.cruxframework.crux.module.validation;

import java.util.regex.Pattern;

import org.cruxframework.crux.core.utils.RegexpPatterns;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DecimalNotationVersionChecker implements CruxModuleVersionChecker
{
    public static final Pattern REGEXP_DOT_NUMBER = Pattern.compile("[\\.0-9 ]+");
    public static final Pattern REGEXP_SPACE_OR_DOT = Pattern.compile("[\\. ]+");
    
    /**
     * 
     */
	public boolean checkMaxVersion(String expectedVersion, String realVersion)
	{
    	checkVersionFormats(expectedVersion, realVersion);
		if (expectedVersion == null || expectedVersion.trim().length() == 0)
		{
			return true;
		}
    	
    	if (realVersion.startsWith(expectedVersion) && realVersion.length() > expectedVersion.length())
    	{
    		return Integer.parseInt(REGEXP_SPACE_OR_DOT.matcher(realVersion).replaceAll("")) == 0;
    	}
    	
    	String[] expectedVersionNumbers = RegexpPatterns.REGEXP_DOT.split(expectedVersion);
    	String[] realVersionNumbers = RegexpPatterns.REGEXP_DOT.split(realVersion);
    	
    	for (int i=0; i<expectedVersionNumbers.length; i++)
    	{
    		if (i < realVersionNumbers.length)
    		{
    			int realVal = Integer.parseInt(realVersionNumbers[i].trim());
				int expectedVal = Integer.parseInt(expectedVersionNumbers[i].trim());
				if (realVal > expectedVal)
    			{
    				return false;
    			}
				else if (realVal < expectedVal)
				{
					return true;
				}
    		}
    		else
    		{
    			break;
    		}
    	}
    	
    	return true;
	}

    /**
     * 
     */
	public boolean checkMinVersion(String expectedVersion, String realVersion)
	{
    	checkVersionFormats(expectedVersion, realVersion);
		if (expectedVersion == null || expectedVersion.trim().length() == 0)
		{
			return true;
		}
    	
    	if (realVersion.startsWith(expectedVersion))
    	{
    		return true;
    	}
    	
    	String[] expectedVersionNumbers = RegexpPatterns.REGEXP_DOT.split(expectedVersion);
    	String[] realVersionNumbers = RegexpPatterns.REGEXP_DOT.split(realVersion);
    	
    	for (int i=0; i<expectedVersionNumbers.length; i++)
    	{
    		if (i < realVersionNumbers.length)
    		{
    			int realVal = Integer.parseInt(realVersionNumbers[i].trim());
				int expectedVal = Integer.parseInt(expectedVersionNumbers[i].trim());
				if (realVal < expectedVal)
    			{
    				return false;
    			}
				else if (realVal > expectedVal)
				{
					return true;
				}
    		}
    		else
    		{
    			break;
    		}
    	}
    	
    	return true;	
    }
	
	/**
	 * 
	 * @param expectedVersion
	 * @param realVersion
	 */
	private void checkVersionFormats(String expectedVersion, String realVersion)
	{
		if (realVersion == null || realVersion.trim().length() == 0)
		{
			throw new NullPointerException("Module version is empty.");
		}
		
		if (expectedVersion != null && expectedVersion.length() > 0 && !REGEXP_DOT_NUMBER.matcher(expectedVersion).matches())
    	{
    		throw new InvalidVersionFormatException(expectedVersion);
    	}
    	if (!REGEXP_DOT_NUMBER.matcher(realVersion).matches())
    	{
    		throw new InvalidVersionFormatException(realVersion);
    	}
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class InvalidVersionFormatException extends RuntimeException
	{
		private static final long serialVersionUID = 115189945240905041L;

		public InvalidVersionFormatException()
		{
			super();
		}

		public InvalidVersionFormatException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public InvalidVersionFormatException(String message)
		{
			super(message);
		}

		public InvalidVersionFormatException(Throwable cause)
		{
			super(cause);
		}
	}
}
