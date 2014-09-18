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
package org.cruxframework.crux.core.rebind.rest;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PathUtils
{
	public static String getSegmentParameter(String segment)
	{
		String value = null;
		if (segment != null)
		{
			int indexOfExpression = segment.indexOf(':');
			if (indexOfExpression > 0)
			{
				int closeParam = segment.indexOf('}',indexOfExpression);
				value = segment.substring(0, indexOfExpression) + segment.substring(closeParam); 
			}
			else
			{
				value = segment;
			}
		}
		return value;
	}
}
