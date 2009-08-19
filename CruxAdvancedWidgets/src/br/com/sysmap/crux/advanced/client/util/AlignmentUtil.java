package br.com.sysmap.crux.advanced.client.util;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class AlignmentUtil
{
	public static HorizontalAlignmentConstant getHorizontalAlignment(String align, HorizontalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.trim().length() > 0)
		{
			if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(align))
			{
				return HasHorizontalAlignment.ALIGN_CENTER;
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(align))
			{
				return HasHorizontalAlignment.ALIGN_DEFAULT;
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(align))
			{
				return HasHorizontalAlignment.ALIGN_LEFT;
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(align))
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
	
	public static VerticalAlignmentConstant getVerticalAlignment(String align, VerticalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.trim().length() > 0)
		{
			if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(align))
			{
				return HasVerticalAlignment.ALIGN_BOTTOM;
			}
			else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(align))
			{
				return HasVerticalAlignment.ALIGN_MIDDLE;
			}
			else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(align))
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
	
	
}
