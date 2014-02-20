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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 * @Deprecated Use org.cruxframework.crux.core.client.animation.Animation instead
 */
@Deprecated
public class Animation
{
	/**
	 * 
	 * @param widget
	 * @param diff
	 * @param callback
	 */
	public static void translateX(Widget widget, int diff, Callback callback)
	{
		org.cruxframework.crux.core.client.animation.Animation.translateX(widget, diff, callback);
	}
	
	/**
	 * 
	 * @param widget
	 * @param diff
	 * @param duration
	 * @param callback
	 */
	public static void translateX(Widget widget, int diff, int duration, Callback callback)
	{
		org.cruxframework.crux.core.client.animation.Animation.translateX(widget, diff, duration, callback);
	}
	
	/**
	 * 
	 * @param widget
	 * @param height
	 * @param duration
	 * @param callback
	 */
	public static void setHeight(Widget widget, String height, int duration, Callback callback)
	{
		org.cruxframework.crux.core.client.animation.Animation.setHeight(widget, height, duration, callback);
	}

	/**
	 * 
	 * @param widget
	 * @param height
	 * @param duration
	 * @param callback
	 */
	public static void setHeight(Widget widget, int height, int duration, Callback callback)
	{
		org.cruxframework.crux.core.client.animation.Animation.setHeight(widget, height, duration, callback);
	}

	/**
	 * 
	 * @param widget
	 */
	public static void resetTransition(Widget widget)
	{
		org.cruxframework.crux.core.client.animation.Animation.resetTransition(widget);
	}

	/**
	 * 
	 * @param widget
	 */
	public static void hideBackface(Widget widget)
	{
		org.cruxframework.crux.core.client.animation.Animation.hideBackface(widget);
	}
	
	/**
	 * 
	 * @param outWidget
	 * @param inWidget
	 * @param duration
	 * @param callback
	 */
	public static void fade(final Widget outWidget, final Widget inWidget, final int duration, final Callback callback)
	{
		inWidget.getElement().getStyle().setOpacity(0);
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				org.cruxframework.crux.core.client.animation.Animation.fade(outWidget, inWidget, duration, callback);
			}
		});
	}
	
	/**
	 * 
	 * @param outWidget
	 * @param duration
	 * @param callback
	 */
	public static void fadeOut(Widget outWidget, int duration, Callback callback)
	{
		org.cruxframework.crux.core.client.animation.Animation.fadeOut(outWidget, duration, callback);
	}
	
	/**
	 * 
	 * @param widget
	 */
	public static void clearFadeTransitions(Widget widget)
    {
		org.cruxframework.crux.core.client.animation.Animation.clearFadeTransitions(widget);
    }
	
	/**
	 * 
	 * @param inWidget
	 * @param duration
	 * @param callback
	 */
	public static void fadeIn(final Widget inWidget, final int duration, final Callback callback)
	{
		inWidget.getElement().getStyle().setOpacity(0);
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				org.cruxframework.crux.core.client.animation.Animation.fadeIn(inWidget, duration, callback);
			}
		});
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback extends org.cruxframework.crux.core.client.animation.Animation.Callback
	{
	}

}