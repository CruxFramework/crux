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
