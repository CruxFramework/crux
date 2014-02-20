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
package org.cruxframework.crux.core.rebind.screen.widget.creator.align;

import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AlignmentAttributeParser
{
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static String getHorizontalAlignment(String align, String defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			HorizontalAlignment alignment = HorizontalAlignment.valueOf(align);
			
			switch (alignment) 
			{
				case center: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER";
				case left: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_LEFT";
				case right: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_RIGHT";
				case justify: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_JUSTIFY";
				case localeStart: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_LOCALE_START";
				case localeEnd: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_LOCALE_END";
				case defaultAlign: return HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT";

				default: return defaultAlignment;
			}
		}
		else
		{
			return defaultAlignment;
		}
	}
	
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static String getAutoHorizontalAlignment(String align, String defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			AutoHorizontalAlignment alignment = AutoHorizontalAlignment.valueOf(align);
			
			switch (alignment) 
			{
				case center: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER";
				case left: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_LEFT";
				case right: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_RIGHT";
				case justify: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_JUSTIFY";
				case localeStart: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_LOCALE_START";
				case localeEnd: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_LOCALE_END";
				case contentStart: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_CONTENT_START";
				case contentEnd: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_CONTENT_END";
				case defaultAlign: return HasAutoHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT";

				default: return defaultAlignment.toString();
			}
		}
		else
		{
			return defaultAlignment;
		}
	}
	
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static String getVerticalAlignment(String align, String defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			VerticalAlignment alignment = VerticalAlignment.valueOf(align);

			if (alignment.equals(VerticalAlignment.bottom))
			{
				return HasVerticalAlignment.class.getCanonicalName()+".ALIGN_BOTTOM";
			}
			else if (alignment.equals(VerticalAlignment.middle))
			{
				return HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE";
			}
			else if (alignment.equals(VerticalAlignment.top))
			{
				return HasVerticalAlignment.class.getCanonicalName()+".ALIGN_TOP";
			}
			else
			{
				return defaultAlignment;
			}
		}
		else
		{
			return defaultAlignment;
		}
	}		
	
	/**
	 * 
	 * @param align
	 * @return
	 */
	public static String getVerticalAlignment(String align)
	{
		return getVerticalAlignment(align, HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE");
	}
}
