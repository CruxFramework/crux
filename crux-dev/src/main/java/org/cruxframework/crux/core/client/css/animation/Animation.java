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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.utils.DOMUtils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class to create animations on top of CSS3 animations.
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class Animation <T extends CssResource>
{
	private static BrowserImpl browserImpl;

	/**
	 * Constructor
	 */
	public Animation()
    {
		getCssResource().ensureInjected();
    }
	
	/**
	 * Run the animation on the given widget
	 * @param widget to be animated
	 */
	public void animate(Widget widget)
	{
		animate(widget.getElement(), null);
	}

	/**
	 * Run the animation on the given element
	 * @param el to be animated
	 */
	public void animate(final Element el)
	{
		animate(el, null);
	}
	
	/**
	 * Run the animation on the given widget
	 * @param widget to be animated
	 * @param callback called when animation is completed
	 */
	public void animate(Widget widget, final Callback callback)
	{
		animate(widget.getElement(), callback);
	}
	
	/**
	 * Run the animation on the given element
	 * @param el to be animated
	 * @param callback called when animation is completed
	 */
	public void animate(final Element el, final Callback callback)
	{
		DOMUtils.addOneTimeHandler(el, getBrowserImpl().getAnimationEndFunctioName(), new DOMUtils.EvtHandler()
		{
			@Override
			public void onEvent(NativeEvent evt)
			{
				if (callback != null)
				{
					try
					{
						callback.onAnimationCompleted();
					}
					catch(Exception e)
					{
						Crux.getErrorHandler().handleError(e);
					}
				}
				el.removeClassName(getAnimationCssTrigger()+" "+getAnimationName());
			}
		});
		el.addClassName(getAnimationCssTrigger()+" "+getAnimationName());
	}
	
	/**
	 * Retrieve the name of the css class used to trigger the animation
	 * @return
	 */
	protected String getAnimationCssTrigger()
	{
		return "animated";
	}

	/**
	 * Retrieve the name of the css animation rule used to animate the element
	 * @return
	 */
	protected abstract String getAnimationName();

	/**
	 * Retrieve the CssResource that declare the animation rules used by this animation.
	 * @return
	 */
	protected abstract T getCssResource();
	
	/**
	 * Callback used to monitor when the animation is completed
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback
	{
		/**
		 * Called when the animation is completed
		 */
		void onAnimationCompleted();
	}
	
	static BrowserImpl getBrowserImpl()
	{
		if (browserImpl == null)
		{
			browserImpl = GWT.create(BrowserImpl.class);
		}
		return browserImpl;
	}
	
	static class BrowserImpl
	{
		public String getAnimationEndFunctioName()
		{
			return "animationend";
		}
	}
	
	static class WebkitBrowserImpl extends BrowserImpl
	{
		@Override
        public String getAnimationEndFunctioName()
        {
	        return "webkitAnimationEnd";
        }
	}
	
	static class MozilaBrowserImpl extends BrowserImpl
	{
		@Override
        public String getAnimationEndFunctioName()
        {
	        return "mozAnimationEnd";
        }
	}
	
	static class MSBrowserImpl extends BrowserImpl
	{
		@Override
        public String getAnimationEndFunctioName()
        {
	        return "MSAnimationEnd";
        }
	}
}
