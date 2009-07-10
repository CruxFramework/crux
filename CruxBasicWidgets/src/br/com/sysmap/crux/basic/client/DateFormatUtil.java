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
package br.com.sysmap.crux.basic.client;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Helper class for handle date formating.
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class DateFormatUtil
{
	public static final String SHORT_TIME_PATTERN = "shortTime";
	public static final String SHORT_DATE_TIME_PATTERN = "shortDateTime";
	public static final String SHORT_DATE_PATTERN = "shortDate";
	public static final String MEDIUM_TIME_PATTERN = "mediumTime";
	public static final String MEDIUM_DATE_TIME_PATTERN = "mediumDateTime";
	public static final String MEDIUM_DATE_PATTERN = "mediumDate";
	public static final String LONG_TIME_PATTERN = "longTime";
	public static final String LONG_DATE_TIME_PATTERN = "longDateTime";
	public static final String LONG_DATE_PATTERN = "longDate";
	public static final String FULL_TIME_PATTERN = "fullTime";
	public static final String FULL_DATE_TIME_PATTERN = "fullDateTime";
	public static final String FULL_DATE_PATTERN = "fullDate";

	/**
	 * Gets a DateTimeFormat object based on the patternString parameter. 
	 * @param patternString
	 * @return
	 */
	public static DateTimeFormat getDateTimeFormat(String patternString)
	{
		DateTimeFormat result;
		
		if (FULL_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFullDateFormat();
		}
		else if (FULL_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFullDateTimeFormat();
		}
		else if (FULL_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFullTimeFormat();
		}
		else if (LONG_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getLongDateFormat();
		}
		else if (LONG_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getLongDateTimeFormat();
		}
		else if (LONG_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getLongTimeFormat();
		}
		else if (MEDIUM_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getMediumDateFormat();
		}
		else if (MEDIUM_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getMediumDateTimeFormat();
		}
		else if (MEDIUM_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getMediumTimeFormat();
		}
		else if (SHORT_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getShortDateFormat();
		}
		else if (SHORT_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getShortDateTimeFormat();
		}
		else if (SHORT_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getShortTimeFormat();
		}
		else
		{
			result = DateTimeFormat.getFormat(patternString);
		}
		
		return result;
	}

}
