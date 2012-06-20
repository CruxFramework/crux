package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JSHorizontalSlidingSwapAnimation extends HorizontalSlidingSwapAnimation
{
	private class JSAnimation extends Animation
	{
		private Element entering;
		private Element leaving;
		private int enteringInitialPos;
		private int leavingInitialPos;

		private JSAnimation(Element entering, Element leaving)
        {
			this.entering = entering;
			this.leaving = leaving;
			this.enteringInitialPos = entering.getAbsoluteLeft();
			this.leavingInitialPos = leaving.getAbsoluteLeft();
        }
		
		@Override
		protected void onUpdate(double progress)
		{
			entering.getStyle().setLeft(enteringInitialPos+(delta*progress), Unit.PX);
			leaving.getStyle().setLeft(leavingInitialPos+(delta*progress), Unit.PX);
		}
		
		@Override
		protected void onComplete()
		{
		    onUpdate(1);
		}
		
		private void reset()
		{
		    onUpdate(0);
		}
	}
	
	private JSAnimation jsAnimation;;
	
	@Override
	protected void doStart() 
	{
		int durationMillis = getDurationMillis();
		this.jsAnimation = new JSAnimation(getEntering(), getLeaving());
		jsAnimation.run(durationMillis);
	}

	@Override
	public void doFinish() 
	{
		jsAnimation.cancel();
		jsAnimation.onComplete();
	}
	
	@Override
	public void doCancel() 
	{
		jsAnimation.cancel();
		jsAnimation.reset();
	}

	@Override
	protected void beforeStart(Element entering, Element leaving)
	{
	}
	
	@Override
	protected void setPositionDelta(Element target, int delta, int durationMillis) 
	{
	}
}