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
package org.cruxframework.crux.widgets.client.animation;

import org.cruxframework.crux.widgets.client.animation.Animation.CompleteCallback;
import org.cruxframework.crux.widgets.client.animation.SlidingSwapAnimation.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AnimationFactory
{
	public static HorizontalSlidingSwapAnimation createHorizontalSlidingSwapAnimation(Element entering, Element leaving, 
			               										int delta, Direction direction, int durationMillis, CompleteCallback completeCallback)
	{
		HorizontalSlidingSwapAnimation animation = GWT.create(HorizontalSlidingSwapAnimation.class);
		animation.setEntering(entering);
		animation.setLeaving(leaving);
		animation.setDelta(delta, direction);
		animation.setDurationMillis(durationMillis);
		animation.setCompleteCallback(completeCallback);
		return animation;
	}
}
