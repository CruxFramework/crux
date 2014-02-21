package org.cruxframework.crux.core.utils;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

public class ViewUtils
{
	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	public static TagConstraints getChildTagConstraintsAnnotation(Class<?> processorClass)
	{
		TagConstraints attributes = processorClass.getAnnotation(TagConstraints.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && !superClass.equals(WidgetCreator.class))
			{
				attributes = getChildTagConstraintsAnnotation(superClass);
			}
		}

		return attributes;
	}
}
