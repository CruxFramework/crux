package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * @author Gesse Dafe
 */
public abstract class SlidingSwapAnimation extends SwapAnimation
{
	protected int orientation;
	protected int delta;
	public static enum Direction {FORWARD, BACKWARDS}

	public void setDelta(int delta, Direction direction)
	{
		this.orientation = direction.equals(Direction.FORWARD) ? -1 : 1;
		this.delta = delta * orientation;
	}
	
	@Override
	protected void doStart() 
	{
		int durationMillis = getDurationMillis();
		beforeStart(getEntering(), getLeaving());
		
		setPositionDelta(getEntering(), delta, durationMillis);
		setPositionDelta(getLeaving(), delta, durationMillis);
	}

	@Override
	public void doFinish() 
	{
		setPositionDelta(getEntering(), delta, 0);
		setPositionDelta(getLeaving(), delta, 0);
	}
	
	@Override
	public void doCancel() 
	{
		setPositionDelta(getEntering(), 0, 0);
		setPositionDelta(getLeaving(), 0, 0);
	}

	protected abstract void beforeStart(Element entering, Element leaving);
	protected abstract void setPositionDelta(Element target, int delta, int durationMillis);
	protected abstract int getOriginalPosition(Element element);
}