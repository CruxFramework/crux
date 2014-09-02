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
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class OTransitionHandler extends BaseTransitionHandler
{
	@Override
	public void hideBackface(Widget widget)
	{
	}

	protected native void fadeOut(Element el, double duration)/*-{
		el.style.oTransitionProperty = 'opacity';
		el.style.oTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.OTransitionDuration = '';
			el.style.OTransitionTimingFunction = '';
		}
		else
		{
			el.style.OTransitionDuration = duration+'ms';
			el.style.OTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 0;
	}-*/;

	protected native void fadeIn(Element el, double duration, double delay)/*-{
		el.style.oTransitionProperty = 'opacity';
		if (duration == 0)
		{
			el.style.oTransitionDelay = '0';
			el.style.OTransitionDuration = '';
			el.style.OTransitionTimingFunction = '';
		}
		else
		{
			el.style.oTransitionDelay = ''+delay;
			el.style.OTransitionDuration = duration+'ms';
			el.style.OTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 1;
	}-*/;

	protected native void setHeight(Element el, String height, int duration)/*-{
		el.style.oTransitionProperty = 'height';
		el.style.oTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.OTransitionDuration = '';
			el.style.OTransitionTimingFunction = '';
		}
		else
		{
			el.style.OTransitionDuration = duration+'ms';
			el.style.OTransitionTimingFunction = 'ease-out';
		}

		el.style.height = height;
	}-*/;

	protected native void clearTransitionProperties(Element el)/*-{
		el.style.oTransitionProperty = 'all';
		el.style.OTransitionDuration = '';
		el.style.OTransitionTimingFunction = '';
	}-*/;

	protected native void translateX(Element el, int diff)/*-{
		el.style.OTransitionProperty = 'all';
		el.style.OTransitionDuration = '';
		el.style.OTransitionTimingFunction = '';
		el.style.OTransitionDelay = '0';
		el.style.OTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void translateX(Element el, int diff, int duration)/*-{
		el.style.OTransitionProperty = 'all';
		el.style.OTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.OTransitionDuration = '';
			el.style.OTransitionTimingFunction = '';
		}
		else
		{
			el.style.OTransitionDuration = duration+'ms';
			el.style.OTransitionTimingFunction = 'ease-out';
		}

		el.style.OTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	protected native void resetTransition(Element el)/*-{
		el.style.OTransform = 'translate(0px,0px)';
	}-*/;
}