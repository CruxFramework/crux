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
package org.cruxframework.crux.widgets.client.swappanel;
 
import org.cruxframework.crux.widgets.client.animation.Animation.CompleteCallback;
import org.cruxframework.crux.widgets.client.animation.AnimationFactory;
import org.cruxframework.crux.widgets.client.animation.HorizontalSlidingSwapAnimation;
import org.cruxframework.crux.widgets.client.animation.SlidingSwapAnimation.Direction;
import org.cruxframework.crux.widgets.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that swaps its contents using slide animations.
 * @author Gesse Dafe
 */
public class HorizontalSwapPanel extends Composite implements HasSwapHandlers
{
	private FlowPanel mainContainer;
	
	private FlowPanel currentPanel = new FlowPanel();
	private FlowPanel nextPanel = new FlowPanel();
	
	private HorizontalSlidingSwapAnimation animation;
	private int transitionDuration = 500;
	
	/**
	 * Constructor
	 */
	public HorizontalSwapPanel() 
	{
		mainContainer = new FlowPanel();
		
		initWidget(mainContainer);
		
		Style style = getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.HIDDEN);
		
		configureCurrentPanel();
		configureNextPanel();
		
		mainContainer.add(currentPanel);
		mainContainer.add(nextPanel);
	}

	@Override
    public HandlerRegistration addSwapHandler(SwapHandler handler)
    {
	    return addHandler(handler, SwapEvent.getType());
    }
	
	/**
	 * Sets the widget that will be initially visible on this panel. 
	 * @param widget
	 */
	public void setCurrentWidget(Widget widget) 
	{
		this.currentPanel.clear();
		this.currentPanel.add(widget);
	}

	/**
	 * 
	 * @return
	 */
	public Widget getCurrentWidget()
	{
		return this.currentPanel.getWidgetCount() > 0 ? this.currentPanel.getWidget(0) : null;
	}
	
	/**
	 * Changes the widget being shown on this widget.
	 * @param w
	 * @param direction the direction of the animation
	 */
	public void transitTo(Widget w, final Direction direction) 
	{
		transitTo(w, direction, null);
	}
	/**
	 * Changes the widget being shown on this widget.
	 * @param w
	 * @param direction the direction of the animation
	 * @param completeCallback
	 */
	public void transitTo(Widget w, final Direction direction, final CompleteCallback completeCallback) 
	{
		prepareNextPanelToSlideIn(w, direction);
		freezeContainerHeight();
		prepareCurrentPanelToSlideOut();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				slide(direction, completeCallback);
			}
		});
	}

	/**
	 * Sets the duration of the animations in milliseconds.
	 * @param transitionDuration
	 */
	public void setTransitionDuration(int transitionDuration) 
	{
		this.transitionDuration = transitionDuration;
	}
	
	private void configureCurrentPanel() 
	{
		if (animation != null)
		{
			animation.cancel();
		}
		Style style = currentPanel.getElement().getStyle();
		style.setProperty("left", "auto");
		style.setProperty("top", "auto");
		style.setPosition(Position.RELATIVE);
		style.setWidth(100, Unit.PCT);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.VISIBLE);
		style.setVisibility(Visibility.VISIBLE);
		style.setOpacity(1);
		//currentPanel.setVisible(true);
	}

	private void configureNextPanel() 
	{
		Style style = nextPanel.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setWidth(100, Unit.PCT);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.VISIBLE);
//		style.setTop(0, Unit.PX);
//		style.setLeft(0, Unit.PX);
		style.setVisibility(Visibility.HIDDEN);
		style.setOpacity(0);
		//nextPanel.setVisible(false);
	}
	
	private void prepareNextPanelToSlideIn(Widget w, Direction direction) 
	{
		nextPanel.clear();
		nextPanel.add(w);
		int left;
		
		if(direction.equals(Direction.FORWARD))
		{
			left = getOffsetWidth();
		}
		else
		{
			left = -getOffsetWidth();
		}
		
		Style style = nextPanel.getElement().getStyle();
		style.setLeft(left, Unit.PX);
		style.setVisibility(Visibility.VISIBLE);
		style.setOpacity(1);

	}

	private void prepareCurrentPanelToSlideOut() 
	{
		currentPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}

	private void freezeContainerHeight() 
	{
		getElement().getStyle().setHeight(currentPanel.getElement().getOffsetHeight(), Unit.PX);
	}
	
	private void concludeSlide(final CompleteCallback completeCallback) 
	{
		final int HEIGHT_TRANSITION_DURATION = 200;
		Style style = getElement().getStyle();
		style.setProperty("webkitTransitionProperty", "height");
		style.setProperty("webkitTransitionDuration", HEIGHT_TRANSITION_DURATION+"ms");
		style.setHeight(currentPanel.getElement().getOffsetHeight(), Unit.PX);
		animation = null;
		
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
		{
			@Override
			public boolean execute() 
			{
				getElement().getStyle().setProperty("webkitTransitionDuration", "0ms");
				setHeight("auto");
				if (completeCallback != null)
				{
					completeCallback.onComplete();
				}
				SwapEvent.fire(HorizontalSwapPanel.this);
				return false;
			}
		}, HEIGHT_TRANSITION_DURATION+10);
	}

	private void slide(Direction direction, final CompleteCallback completeCallback) 
	{
		if(animation != null)
		{
			animation.cancel();
		}
		
		Element entering = nextPanel.getElement();
		Element leaving = currentPanel.getElement();
		
		int delta = this.getElement().getClientWidth();
		animation = AnimationFactory.createHorizontalSlidingSwapAnimation(entering, leaving, delta, direction, transitionDuration, new CompleteCallback()
		{
			@Override
			public void onComplete()
			{
				onTransitionComplete(completeCallback);
			}
		});
		animation.start();
	}
	
	private void onTransitionComplete(CompleteCallback completeCallback)
	{
		FlowPanel temp = nextPanel;
		nextPanel = currentPanel;
		currentPanel = temp;
		
		configureNextPanel();
		configureCurrentPanel();
		concludeSlide(completeCallback);
	}
	
	public void clear()
	{
		currentPanel.clear();
		nextPanel.clear();
	}
}