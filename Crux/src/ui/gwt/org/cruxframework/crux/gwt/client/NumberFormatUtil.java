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

import com.google.gwt.i18n.client.NumberFormat;

/**
 * Helper class for handle number formating.
 * @author Thiago da Rosa de Bustamante
 */
public class NumberFormatUtil
{

	public static final String DECIMAL_PATTERN = "decimal";
	public static final String CURRENCY_PATTERN = "currency";
	public static final String PERCENT_PATTERN = "percent";
	public static final String SCIENTIFIC_PATTERN = "scientific";

	/**
	 * Gets a NumberFormat object based on the patternString parameter. 
	 * @param patternString
	 * @return
	 */
	public static NumberFormat getNumberFormat(String patternString)
	{
		NumberFormat result;
		
		if (DECIMAL_PATTERN.equals(patternString))
		{
			result = NumberFormat.getDecimalFormat();
		}
		else if (CURRENCY_PATTERN.equals(patternString))
		{
			result = NumberFormat.getCurrencyFormat();
		}
		else if (PERCENT_PATTERN.equals(patternString))
		{
			result = NumberFormat.getPercentFormat();
		}
		else if (SCIENTIFIC_PATTERN.equals(patternString))
		{
			result = NumberFormat.getScientificFormat();
		}
		else
		{
			result = NumberFormat.getFormat(patternString);
		}
		
		return result;
	}

}
