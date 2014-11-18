/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.css.animation;

import com.google.gwt.resources.client.CssResource;

/**
 * Create animations based on animate.css library
 * Animate.css - http://daneden.me/animate
 * Copyright (c) 2013 Daniel Eden

 * @author Thiago da Rosa de Bustamante
 */
public class StandardAnimation extends Animation<CssResource>
{
	public static enum Type{bounce, flash, pulse, rubberBand, shake, swing, tada, wobble, bounceIn, bounceInDown, bounceInLeft, bounceInRight, bounceInUp,
		bounceOut, bounceOutDown, bounceOutLeft, bounceOutRight, bounceOutUp, fadeIn, fadeInDown, fadeInDownBig, fadeInLeft, fadeInLeftBig, fadeInRight,
		fadeInRightBig, fadeInUp, fadeInUpBig, fadeOut, fadeOutDown, fadeOutDownBig, fadeOutLeft, fadeOutLeftBig, fadeOutRight, fadeOutRightBig, fadeOutUp,
		fadeOutUpBig, flip, flipInX, flipInY, flipOutX, flipOutY, lightSpeedIn, lightSpeedOut, rotateIn, rotateInDownLeft, rotateInDownRight, rotateInUpLeft,
		rotateInUpRight, rotateOut, rotateOutDownLeft, rotateOutDownRight, rotateOutUpLeft, rotateOutUpRight, slideInDown, slideInLeft, slideInRight, 
		slideOutLeft, slideOutRight, slideOutUp, hinge, rollIn, rollOut}
	
	private Type animationType = Type.bounce;
	
	public StandardAnimation(Type animationType)
	{
		this.animationType = animationType;
	}
	
	public Type getAnimationType()
	{
		return animationType;
	}

	@Override
    protected CssResource getCssResource()
    {
	    return StandardAnimationResources.INSTANCE.css();
    }

	@Override
    protected String getAnimationName()
    {
	    return animationType.name();
    }
}
