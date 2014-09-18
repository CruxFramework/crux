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
package org.cruxframework.crux.core.client.animation;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;


/**
 * Simple vertical motion animation
 * 
 * @author Gesse Dafe
 */
@Deprecated
@Legacy
public class VerticalMotionAnimation extends Animation
{
	private final Style style;
	private int startTop;
	private int finalTop;
 
	/**
	 * Constructor
	 * @param element Element to be animated
	 */
	public VerticalMotionAnimation(Element element)
	{
		this.style = element.getStyle();
	}
 

	/**
	 * Move the animated element from start position to final position
	 * @param startLeft start top position
	 * @param finalLeft final top position
	 * @param milliseconds animation duration
	 */
	public void move(int startTop, int finalTop, int milliseconds)
	{
		this.startTop = startTop;
		this.finalTop = finalTop;
		run(milliseconds);
	}
 
	@Override
	protected void onUpdate(double progress)
	{
		double top = startTop + (progress * (finalTop - startTop));
 		this.style.setTop(top, Unit.PX);
	}	
 
	@Override
	protected void onComplete()
	{
		super.onComplete();
 		this.style.setTop(finalTop, Unit.PX);
	}
}