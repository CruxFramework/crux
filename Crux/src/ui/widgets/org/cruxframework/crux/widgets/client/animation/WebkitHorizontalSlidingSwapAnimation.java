package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * @author Gesse Dafe
 */
public class WebkitHorizontalSlidingSwapAnimation extends HorizontalSlidingSwapAnimation
{
	@Override
	protected native void beforeStart(Element entering, Element leaving) /*-{
		entering.style.webkitTransitionProperty = '-webkit-transform';
		leaving.style.webkitTransitionProperty = '-webkit-transform';
	}-*/;

	@Override
	protected native void setPositionDelta(Element target, int delta, int durationMillis) /*-{
		target.style.webkitTransitionDuration = durationMillis + 'ms';
		target.style.webkitTransform = 'translate3d(' + delta + 'px,0,0)';
	}-*/;
}