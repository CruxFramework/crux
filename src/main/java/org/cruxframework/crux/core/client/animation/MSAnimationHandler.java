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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class MSAnimationHandler extends BaseAnimationHandler
{

	@Override
	public void hideBackface(Widget widget)
	{
	}

	protected native void fadeOut(Element el, double duration)/*-{
		el.style.msTransitionProperty = 'opacity';
		el.style.msTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.msTransitionDuration = '';
			el.style.msTransitionTimingFunction = '';
		}
		else
		{
			el.style.msTransitionDuration = duration+'ms';
			el.style.msTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 0;
	}-*/;

	protected native void fadeIn(Element el, double duration, double delay)/*-{
		el.style.msTransitionProperty = 'opacity';
		if (duration == 0)
		{
			el.style.msTransitionDelay = '0';
			el.style.msTransitionDuration = '';
			el.style.msTransitionTimingFunction = '';
		}
		else
		{
			el.style.msTransitionDelay = ''+delay;
			el.style.msTransitionDuration = duration+'ms';
			el.style.msTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 1;
	}-*/;

	protected native void setHeight(Element el, String height, int duration)/*-{
		el.style.msTransitionProperty = 'height';
		el.style.msTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.msTransitionDuration = '';
			el.style.msTransitionTimingFunction = '';
		}
		else
		{
			el.style.msTransitionDuration = duration+'ms';
			el.style.msTransitionTimingFunction = 'ease-out';
		}

		el.style.height = height;
	}-*/;

	protected native void clearTransitionProperties(Element el)/*-{
		el.style.msTransitionProperty = 'all';
		el.style.msTransitionDuration = '';
		el.style.msTransitionTimingFunction = '';
	}-*/;

	protected native void translateX(Element el, int diff)/*-{
		el.style.msTransitionProperty = 'all';//-webkit-transform
		el.style.msTransitionDuration = '';
		el.style.msTransitionTimingFunction = '';
		el.style.msTransitionDelay = '0';
		el.style.msTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void translateX(Element el, int diff, int duration)/*-{
		el.style.msTransitionProperty = 'all';//-webkit-transform
		el.style.msTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.msTransitionDuration = '';
			el.style.msTransitionTimingFunction = '';
		}
		else
		{
			el.style.msTransitionDuration = duration+'ms';
			el.style.msTransitionTimingFunction = 'ease-out';
		}

		el.style.msTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void resetTransition(Element el)/*-{
		el.style.msTransform = 'translate(0px,0px)';
	}-*/;
}