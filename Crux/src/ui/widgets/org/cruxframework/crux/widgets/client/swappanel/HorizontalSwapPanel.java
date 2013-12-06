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
 
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.animation.Animation;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that swaps its contents using slide animations.
 * @author Gesse Dafe
 */
public class HorizontalSwapPanel extends Composite implements HasSwapHandlers
{
	public static enum Direction{FORWARD, BACKWARDS}
	
	private FlowPanel contentPanel;
	
	private SimplePanel currentPanel = new SimplePanel();
	private SimplePanel nextPanel = new SimplePanel();
	private int transitionDuration = 500;
	private boolean useFadeTransitions = false;
	private String height;
	
	/**
	 * Constructor
	 */
	public HorizontalSwapPanel() 
	{
		contentPanel = new FlowPanel();
		initWidget(contentPanel);
		setStyleName("crux-HorizontalSwapPanel");
		
		Style style = contentPanel.getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setOverflow(Overflow.HIDDEN);
		style.setWidth(100, Unit.PCT);
		style.setVisibility(Visibility.VISIBLE);
		style.setOpacity(1);
		
		configureCurrentPanel();
		configureNextPanel();
		
		Animation.hideBackface(currentPanel);
		Animation.hideBackface(nextPanel);
		
		contentPanel.add(currentPanel);
		contentPanel.add(nextPanel);
	}

	@Override
	public void setHeight(String height)
	{
	    this.height = height;
		super.setHeight(height);
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
	public void setCurrentWidget(final Widget widget) 
	{
		if (this.useFadeTransitions)
		{
			final Widget outWidget = this.currentPanel.getWidget();
			if (outWidget != null)
			{
				final int duration = transitionDuration / 2;
				Animation.fadeOut(outWidget, duration, new Callback()
				{
					@Override
					public void onTransitionCompleted()
					{
						currentPanel.clear();
						currentPanel.add(widget);
						Animation.clearFadeTransitions(outWidget);
						Animation.fadeIn(widget, duration, null);
					}
				});
			}
			else
			{
				this.currentPanel.clear();
				this.currentPanel.add(widget);
			}
		}
		else
		{
			this.currentPanel.clear();
			this.currentPanel.add(widget);
		}
	}
	
	/**
	 * Get the useFadeTransitions property. When this property is true, this panel will 
	 * use a fade animation when setCurrentWidget is called
	 * @return
	 */
	public boolean isUseFadeTransitions()
    {
    	return useFadeTransitions;
    }

	/**
	 * Set the useFadeTransitions property. When this property is true, this panel will 
	 * use a fade animation when setCurrentWidget is called
	 * 
	 * @param useFadeTransitions
	 */
	public void setUseFadeTransitions(boolean useFadeTransitions)
    {
    	this.useFadeTransitions = useFadeTransitions;
    }

	/**
	 * 
	 * @return
	 */
	public Widget getCurrentWidget()
	{
		return this.currentPanel.getWidget();
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
	public void transitTo(Widget w, final Direction direction, final Callback completeCallback) 
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
	
	/**
	 * 
	 * @return
	 */
	public int getTransitionDuration()
	{
		return transitionDuration;
	}
	
	public void clear()
	{
		currentPanel.clear();
		nextPanel.clear();
	}

	private void configureCurrentPanel() 
	{
		Animation.resetTransition(currentPanel);

		Style style = currentPanel.getElement().getStyle();

		style.setPosition(Position.RELATIVE);
		style.setTop(0, Unit.PX);
		style.setLeft(0, Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.VISIBLE);
		style.setVisibility(Visibility.VISIBLE);
		style.setOpacity(1);
	}

	private void configureNextPanel() 
	{
		Style style = nextPanel.getElement().getStyle();
		style.setTop(0, Unit.PX);
		style.setLeft(0, Unit.PX);
		style.setPosition(Position.ABSOLUTE);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.VISIBLE);
		style.setVisibility(Visibility.HIDDEN);
		style.setOpacity(0);
	}
	
	private void prepareCurrentPanelToSlideOut() 
	{
		nextPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		
	}
	
	private void prepareNextPanelToSlideIn(Widget w, Direction direction) 
	{
		nextPanel.clear();
		nextPanel.add(w);
		int left;
		
		if(direction.equals(Direction.FORWARD))
		{
			left = contentPanel.getOffsetWidth();
		}
		else
		{
			left = -contentPanel.getOffsetWidth();
		}
		
		Animation.translateX(nextPanel, left, null);
		Style style = nextPanel.getElement().getStyle();
		style.setVisibility(Visibility.VISIBLE);
		style.setOpacity(1);		

	}

	private void freezeContainerHeight() 
	{
		int currentContentHeight = currentPanel.getElement().getOffsetHeight();
		if(currentContentHeight > 0)
		{
			contentPanel.getElement().getStyle().setHeight(currentContentHeight, Unit.PX);
		}
	}
	
	private void concludeSlide(final Callback completeCallback) 
	{
		final int HEIGHT_TRANSITION_DURATION = 80;
		int currentPanelHeight = currentPanel.getWidget() != null ? currentPanel.getWidget().getOffsetHeight() : currentPanel.getOffsetHeight();
		if (contentPanel.getOffsetHeight() != currentPanelHeight)
		{
			Animation.setHeight(contentPanel, currentPanelHeight, HEIGHT_TRANSITION_DURATION, new Callback()
			{
				@Override
				public void onTransitionCompleted()
				{
					Scheduler.get().scheduleDeferred(new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							if (StringUtils.isEmpty(height))
							{
								contentPanel.setHeight("auto");
							}
							else
							{
								contentPanel.setHeight(height);
							}
						}
					});
					if (completeCallback != null)
					{
						completeCallback.onTransitionCompleted();
					}
					SwapEvent.fire(HorizontalSwapPanel.this);
				}
			});
		}
		else
		{
			if (completeCallback != null)
			{
				completeCallback.onTransitionCompleted();
			}
			SwapEvent.fire(HorizontalSwapPanel.this);
		}
	}

	private void slide(Direction direction, final Callback completeCallback) 
	{
		int delta = contentPanel.getElement().getClientWidth();
		if (direction == Direction.FORWARD)
		{
			delta = -delta;
		}
		
		Animation.translateX(currentPanel, delta, transitionDuration, null);
		Animation.translateX(nextPanel, 0, transitionDuration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				onTransitionComplete(completeCallback);
			}
		});
	}
	
	private void onTransitionComplete(final Callback completeCallback)
	{
		SimplePanel temp = nextPanel;
		nextPanel = currentPanel;
		currentPanel = temp;
		
		configureNextPanel();
		configureCurrentPanel();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				concludeSlide(completeCallback);
			}
		});
	}
}