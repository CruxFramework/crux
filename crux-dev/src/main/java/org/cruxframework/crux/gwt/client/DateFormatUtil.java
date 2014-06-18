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
package org.cruxframework.crux.gwt.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * Helper class for handle date formating.
 * @author Thiago da Rosa de Bustamante
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
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL);
		}
		else if (FULL_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL);
		}
		else if (FULL_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.TIME_FULL);
		}
		else if (LONG_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		}
		else if (LONG_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_LONG);
		}
		else if (LONG_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.TIME_LONG);
		}
		else if (MEDIUM_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		}
		else if (MEDIUM_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		}
		else if (MEDIUM_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM);
		}
		else if (SHORT_DATE_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		}
		else if (SHORT_DATE_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
		}
		else if (SHORT_TIME_PATTERN.equals(patternString))
		{
			result = DateTimeFormat.getFormat(PredefinedFormat.TIME_SHORT);
		}
		else
		{
			result = DateTimeFormat.getFormat(patternString);
		}
		
		return result;
	}

}
