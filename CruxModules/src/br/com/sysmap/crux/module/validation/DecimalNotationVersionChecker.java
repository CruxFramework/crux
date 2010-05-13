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
package br.com.sysmap.crux.module.validation;

import java.util.regex.Pattern;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.module.CruxModuleMessages;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class DecimalNotationVersionChecker implements CruxModuleVersionChecker
{
    public static final Pattern REGEXP_DOT_NUMBER = Pattern.compile("[\\.0-9 ]+");
    public static final Pattern REGEXP_SPACE_OR_DOT = Pattern.compile("[\\. ]+");
    
    private CruxModuleMessages messages = MessagesFactory.getMessages(CruxModuleMessages.class);
    
    /**
     * 
     */
    @Override
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
	@Override
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
			throw new NullPointerException(messages.decimalVersionCheckerEmptyRealVersion());
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
