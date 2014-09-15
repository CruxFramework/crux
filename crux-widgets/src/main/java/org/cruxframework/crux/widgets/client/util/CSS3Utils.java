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
package org.cruxframework.crux.widgets.client.util;

import org.cruxframework.crux.core.client.utils.StringUtils;

/**
 * Utilities for CSS3 operations
 * @author Gesse Dafe
 */
public class CSS3Utils
{
	/**
	 * Gets the CSS3 name of the given style
	 * @param defaultStyleName
	 * @return
	 */
	public static String getCSS3StyleName(String defaultStyleName)
	{
		StringBuilder str = new StringBuilder();
	
		if(!StringUtils.isEmpty(defaultStyleName))
		{
			String[] parts = defaultStyleName.split("\\s");
			
			for (int i = 0; i < parts.length; i++)
			{
				String part = parts[i];
				if(!part.endsWith("CSS3"))
				{
					part = part + "CSS3";
				}
				
				if(i > 0)
				{
					str.append(" ");
				}
				str.append(part);
				
			}
		}
		
		return str.toString();		
	}
}
