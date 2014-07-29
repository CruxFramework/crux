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
package org.cruxframework.crux.core.client.css.transition;

import com.google.gwt.dom.client.Element;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class MozTransitionHandler extends BaseTransitionHandler
{
	protected native void fadeOut(Element el, double duration)/*-{
		el.style.MozTransitionProperty = 'opacity';
		el.style.MozTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 0;
	}-*/;

	protected native void fadeIn(Element el, double duration, double delay)/*-{
		el.style.MozTransitionProperty = 'opacity';
		if (duration == 0)
		{
			el.style.MozTransitionDelay = '0';
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDelay = ''+delay;
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 1;
	}-*/;

	protected native void setHeight(Element el, String height, int duration)/*-{
		el.style.MozTransitionProperty = 'height';
		el.style.MozTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}

		el.style.height = height;
	}-*/;

	protected native void clearTransitionProperties(Element el)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDuration = '';
		el.style.MozTransitionTimingFunction = '';
	}-*/;

	protected native void translateX(Element el, int diff)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDuration = '';
		el.style.MozTransitionTimingFunction = '';
		el.style.MozTransitionDelay = '0';
		el.style.MozTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void translateX(Element el, int diff, int duration)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}

		el.style.MozTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void resetTransition(Element el)/*-{
		el.style.MozTransform = 'translate(0px,0px)';
	}-*/;
}