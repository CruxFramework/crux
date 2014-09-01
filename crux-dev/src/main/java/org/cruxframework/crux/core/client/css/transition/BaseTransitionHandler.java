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
package org.cruxframework.crux.core.client.css.transition;

import org.cruxframework.crux.core.client.css.transition.Transition.Callback;
import org.cruxframework.crux.core.client.css.transition.Transition.TransitionHandler;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class BaseTransitionHandler implements TransitionHandler
{
	public void translateX(Widget widget, int diff, Callback callback)
	{
		if(widget == null)
		{
			return;
		}
		
		Element element = widget.getElement();
		if (callback != null)
		{
			addCallbackHandler(element, 0, callback);
		}
		translateX(element, diff);
	}

	public void resetTransition(Widget widget)
	{
		if(widget == null)
		{
			return;
		}
		resetTransition(widget.getElement());
	}

	public void translateX(Widget widget, int diff, int duration, Callback callback)
	{
		if(widget == null)
		{
			return;
		}
		Element element = widget.getElement();
		if (callback != null)
		{
			addCallbackHandler(element, duration, callback);
		}
		translateX(element, diff, duration);
	}
	
	public void setHeight(Widget widget, int height, int duration, Callback callback)
    {
		if(widget == null)
		{
			return;
		}
		setHeight(widget, height+"px", duration, callback);
    }

    public void setHeight(Widget widget, String height, int duration, final Callback callback)
    {
		if(widget == null)
		{
			return;
		}
		final Element element = widget.getElement();
		addCallbackHandler(element, duration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(element);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		setHeight(element, height, duration);
    }

	public void hideBackface(Widget widget)
	{
		if(widget == null)
		{
			return;
		}
		widget.getElement().getStyle().setProperty("webkitBackfaceVisibility", "hidden");
	}

	public void fade(Widget outWidget, Widget inWidget, int duration, final Callback callback)
	{
		if(inWidget == null || outWidget == null)
		{
			return;
		}
		
		final Element outElement = outWidget.getElement();
		final Element inElement = inWidget.getElement();
		addCallbackHandler(outElement, duration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(outElement);
			}
		});
		addCallbackHandler(inElement, duration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(inElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeOut(outElement, (duration/2.0));
		fadeIn(inElement, (duration/2.0), (duration/2.0));
	}
	
	public void fadeOut(Widget outWidget, int duration, final Callback callback)
	{
		if(outWidget == null)
		{
			return;
		}
		
		final Element outElement = outWidget.getElement();
		addCallbackHandler(outElement, duration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(outElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeOut(outElement, duration);
	}

	public void fadeIn(Widget inWidget, int duration, final Callback callback)
	{
		if(inWidget == null)
		{
			return;
		}
		
		final Element inElement = inWidget.getElement();
		addCallbackHandler(inElement, duration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(inElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeIn(inElement, duration, 0);
	}

	public void clearFadeTransitions(Widget widget)
	{
		if(widget == null)
		{
			return;
		}
		
		widget.getElement().getStyle().setOpacity(1);
	}
	
	protected void addCallbackHandler(Element element, int duration, final Callback callback)
	{
		if (callback != null)
		{
			if (duration <= 0)
			{
				callback.onTransitionCompleted();
			}
			else
			{
				new Timer()
				{
					@Override
					public void run()
					{
						callback.onTransitionCompleted();
					}
				}.schedule(duration + 10);
			}
		}
	}
	
	protected abstract void translateX(Element element, int diff);
	protected abstract void setHeight(Element element, String height, int duration);
	protected abstract void translateX(Element element, int diff, int duration);
    protected abstract void resetTransition(Element element);
    protected abstract void clearTransitionProperties(Element element);
	protected abstract void fadeIn(Element inElement, double d, double e);
	protected abstract void fadeOut(Element outElement, double d);
}
