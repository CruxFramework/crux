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
package br.com.sysmap.crux.gwt.client.align;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

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
	public static HorizontalAlignmentConstant getHorizontalAlignment(String align, HorizontalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.trim().length() > 0)
		{
			HorizontalAlignment alignment = HorizontalAlignment.valueOf(align);
			
			if (alignment.equals(HorizontalAlignment.center))
			{
				return HasHorizontalAlignment.ALIGN_CENTER;
			}
			else if (alignment.equals(HorizontalAlignment.defaultAlign))
			{
				return HasHorizontalAlignment.ALIGN_DEFAULT;
			}
			else if (alignment.equals(HorizontalAlignment.left))
			{
				return HasHorizontalAlignment.ALIGN_LEFT;
			}
			else if (alignment.equals(HorizontalAlignment.right))
			{
				return HasHorizontalAlignment.ALIGN_RIGHT;
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
	public static HorizontalAlignmentConstant getHorizontalAlignment(String align)
	{
		return getHorizontalAlignment(align, HasHorizontalAlignment.ALIGN_CENTER);
	}
	
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static VerticalAlignmentConstant getVerticalAlignment(String align, VerticalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.trim().length() > 0)
		{
			VerticalAlignment alignment = VerticalAlignment.valueOf(align);

			if (alignment.equals(VerticalAlignment.bottom))
			{
				return HasVerticalAlignment.ALIGN_BOTTOM;
			}
			else if (alignment.equals(VerticalAlignment.middle))
			{
				return HasVerticalAlignment.ALIGN_MIDDLE;
			}
			else if (alignment.equals(VerticalAlignment.top))
			{
				return HasVerticalAlignment.ALIGN_TOP;
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
	public static VerticalAlignmentConstant getVerticalAlignment(String align)
	{
		return getVerticalAlignment(align, HasVerticalAlignment.ALIGN_MIDDLE);
	}
}
