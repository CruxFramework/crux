package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * Base class for animate swaping 
 * @author Gesse Dafe
 */
public abstract class SwapAnimation extends Animation
{
	private Element entering;
	private Element leaving;
	
	protected Element getEntering() 
	{
		return entering;
	}
	
	protected Element getLeaving() 
	{
		return leaving;
	}

	public void setEntering(Element entering)
    {
    	this.entering = entering;
    }

	public void setLeaving(Element leaving)
    {
    	this.leaving = leaving;
    }
}
