package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * @author Gesse Dafe
 */
public abstract class HorizontalSlidingSwapAnimation extends SlidingSwapAnimation
{
	@Override
	protected int getOriginalPosition(Element element) 
	{
		return element.getOffsetLeft();
	}
}