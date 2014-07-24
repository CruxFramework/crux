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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Transition
{
	private static TransitionHandler transitionHandler = null;

	/**
	 * 
	 * @param widget
	 * @param diff
	 * @param callback
	 */
	public static void translateX(Widget widget, int diff, Callback callback)
	{
		getTransitionHandler().translateX(widget, diff, callback);
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
		getTransitionHandler().translateX(widget, diff, duration, callback);
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
		getTransitionHandler().setHeight(widget, height, duration, callback);
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
		getTransitionHandler().setHeight(widget, height, duration, callback);
	}

	/**
	 * 
	 * @param widget
	 */
	public static void resetTransition(Widget widget)
	{
		getTransitionHandler().resetTransition(widget);
	}

	/**
	 * 
	 * @param widget
	 */
	public static void hideBackface(Widget widget)
	{
		getTransitionHandler().hideBackface(widget);
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
				getTransitionHandler().fade(outWidget, inWidget, duration, callback);
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
		getTransitionHandler().fadeOut(outWidget, duration, callback);
	}
	
	/**
	 * 
	 * @param widget
	 */
	public static void clearFadeTransitions(Widget widget)
    {
		getTransitionHandler().clearFadeTransitions(widget);
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
				getTransitionHandler().fadeIn(inWidget, duration, callback);
			}
		});
	}

	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback
	{
		void onTransitionCompleted();
	}

	public static TransitionHandler getTransitionHandler()
	{
		if (transitionHandler == null)
		{
			if (isIE11())
			{
				transitionHandler = new MSTransitionHandler();
			} else
			{
				transitionHandler = GWT.create(TransitionHandler.class);
			}
		}
		return transitionHandler;
	}
	
	//TODO - remove after upgrading GWT to identify IE11
	/**This method has the function to check if the browser that is being used is IE11. 
	 * As the GWT classify IE11 as a mozilla firefox the effects of 
	 * transition does not work correctly.
	 * 
	 * @author Bruno Medeiros (bruno@triggolabs.com)
	 * @return boolean
	 */
	@Deprecated
	private static native boolean isIE11() /*-{
	try
	{
	  var rv = false;
	  if (navigator.appName == 'Netscape')
	  {
	    var ua = navigator.userAgent;
	    var re  = new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})");
	    if (re.exec(ua) != null)
	      rv = true;
	  }
	  return rv;
	 } catch (err)
	 {
	 	return false;
	 }
	  }-*/;
	
	
	static interface TransitionHandler
	{
		void translateX(Widget widget, int diff, Callback callback);
		void translateX(Widget widget, int diff, int duration, Callback callback);
		void setHeight(Widget widget, int height, int duration, Callback callback);
		void setHeight(Widget widget, String height, int duration, Callback callback);
		void resetTransition(Widget widget);
		void hideBackface(Widget widget);
		void fade(Widget outWidget, Widget inWidget, int duration, Callback callback);
		void fadeOut(Widget outWidget, int duration, Callback callback);
		void fadeIn(Widget InWidget, int duration, Callback callback);
		void clearFadeTransitions(Widget widget);
	}
}