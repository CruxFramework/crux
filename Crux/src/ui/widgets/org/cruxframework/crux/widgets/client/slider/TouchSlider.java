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
package org.cruxframework.crux.widgets.client.slider;

import java.util.Date;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.widgets.client.animation.Animation;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.event.tap.HasTapHandlers;
import org.cruxframework.crux.widgets.client.event.tap.TapEvent;
import org.cruxframework.crux.widgets.client.event.tap.TapHandler;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that swaps its contents using slide animations.
 * @author Thiago da Rosa de Bustamante
 */
public class TouchSlider extends Composite implements HasSwapHandlers, HasSlidingHandlers, HasTapHandlers, 
											TouchStartHandler, TouchMoveHandler, TouchEndHandler, OrientationChangeHandler
{
	private static final int TAP_EVENT_THRESHOLD = 5;
	private static final int SWIPE_THRESHOLD = 50;
	private static final long SWIPE_TIME_THRESHOLD = 250;

	private ContentProvider contentProvider;
	private FocusPanel touchPanel;
	private FlowPanel contentPanel;
	private int currentWidget = -1;
	private int slideTransitionDuration = 500;
	private int startTouchPosition;
	private boolean isSliding = false;
	private long startTouchTime;
	private int currentTouchPosition;
	private HandlerRegistration touchMoveHandler;
	private HandlerRegistration touchEndHandler;
	private boolean circularShowing = false;
	private boolean didMove;

	/**
	 * Constructor
	 */
	public TouchSlider() 
	{
		touchPanel = new FocusPanel();
		contentPanel = new FlowPanel();

		touchPanel.add(contentPanel);
		initWidget(touchPanel);

		Style style = contentPanel.getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setOverflow(Overflow.HIDDEN);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);

		touchPanel.addTouchStartHandler(this);
		
		addAttachHandler(new Handler()
		{
			private HandlerRegistration orientationHandlerRegistration;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					orientationHandlerRegistration = Screen.addOrientationChangeHandler(TouchSlider.this);
				}
				else if (orientationHandlerRegistration != null)
				{
					orientationHandlerRegistration.removeHandler();
					orientationHandlerRegistration = null;
				}
			}
		});
		
	}
	/**
	 * 
	 * @return
	 */
	public ContentProvider getContentProvider()
	{
		return contentProvider;
	}

	/**
	 * 
	 * @param contentProvider
	 */
	public void setContentProvider(ContentProvider contentProvider)
	{
		this.contentProvider = contentProvider;
		contentPanel.clear();
		for (int i = 0; i < contentProvider.size(); i++)
		{
			final int index = i;
			LazyPanel itemWrapper = new LazyPanel()
			{
				@Override
				protected Widget createWidget()
				{
					return TouchSlider.this.contentProvider.loadWidget(index);
				}
			};
			itemWrapper.setStyleName("touchSliderItem");
			Style style = itemWrapper.getElement().getStyle();
			style.setPosition(Position.ABSOLUTE);
			style.setTop(0, Unit.PX);
			style.setLeft(0, Unit.PX);
			style.setWidth(100, Unit.PCT);
			style.setHeight(100, Unit.PCT);
			style.setOverflowX(Overflow.HIDDEN);
			style.setOverflowY(Overflow.VISIBLE);
			contentPanel.add(itemWrapper);
		}
		
		if (this.circularShowing && contentProvider.size() < 3)
		{
			this.circularShowing = false;
		}
	}

	/**
	 * 
	 * @param widget
	 */
	public void add(Widget widget)
	{
		SimplePanel itemWrapper = new SimplePanel();
		itemWrapper.add(widget);
		itemWrapper.setStyleName("touchSliderItem");
		Style style = itemWrapper.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setTop(0, Unit.PX);
		style.setLeft(0, Unit.PX);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.VISIBLE);
		contentPanel.add(itemWrapper);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCircularShowing()
    {
    	return circularShowing;
    }

	/**
	 * 
	 * @param circularShowing
	 */
	public void setCircularShowing(boolean circularShowing)
    {
    	if (contentProvider != null && contentProvider.size() < 3)
    	{
    		this.circularShowing = false;
    	}
    	else
    	{
    		this.circularShowing = circularShowing;
    	}
    }

	/**
	 * 
	 */
	@Override
	public HandlerRegistration addSwapHandler(SwapHandler handler)
	{
		return addHandler(handler, SwapEvent.getType());
	}

	@Override
    public HandlerRegistration addSlidingHandler(SlidingHandler handler)
    {
		return addHandler(handler, SlidingEvent.getType());
    }
	
	@Override
    public HandlerRegistration addTapHandler(TapHandler handler)
    {
		return addHandler(handler, TapEvent.getType());
    }
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Widget getWidget(int index)
	{
		return (contentProvider != null?contentProvider.loadWidget(index):contentPanel.getWidget(index));
	}

	/**
	 * 
	 * @return
	 */
	public int getWidgetCount()
	{
		return (contentProvider != null?contentProvider.size():contentPanel.getWidgetCount());
	}

	/**
	 * 
	 * @param child
	 * @return
	 */
	public int getWidgetIndex(Widget child)
	{
		return contentPanel.getWidgetIndex(child.getParent());
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public boolean remove(int index)
	{
		return contentPanel.remove(index);
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentWidget()
	{
		return currentWidget;
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		if (this.isSliding)
		{
			return;
		}
		if (currentTouchPosition != startTouchPosition)
		{
			final int slideBy = getSlideBy();
			slide(slideBy, false);
		}
		else
		{
			SlidingEvent.fire(this, false);
		}
		if (!didMove)
		{
			TapEvent.fire(this);
		}
		if(touchMoveHandler != null)
		{
			touchMoveHandler.removeHandler();
		}
		if(touchEndHandler != null)
		{
			touchEndHandler.removeHandler();
		}
		event.preventDefault();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		if (this.isSliding)
		{
			return;
		}
		int clientX = event.getTouches().get(0).getClientX();
		int diff = clientX - startTouchPosition;

		boolean hasNextPanel = hasNextWidget();
		boolean hasPreviousPanel = hasPreviousWidget();

		if ((diff < 0 && hasNextPanel) || (diff > 0 && hasPreviousPanel))
		{
			currentTouchPosition = clientX;
			Animation.translateX(getCurrentPanel(), diff, null);
			if (hasPreviousPanel)
			{
				Widget previousPanel = getPreviousPanel();
				Animation.translateX(previousPanel, diff-previousPanel.getOffsetWidth(), null);
			}
			if (hasNextPanel)
			{
				Widget nextPanel = getNextPanel();
				Animation.translateX(nextPanel, diff+nextPanel.getOffsetWidth(), null);
			}
		}
		if (!didMove && (Math.abs(diff) > TAP_EVENT_THRESHOLD))
		{
			didMove = true;
		}
		event.preventDefault();
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		didMove = false;
		event.preventDefault();
		SlidingEvent.fire(this, true);
		startTouchPosition = event.getTouches().get(0).getClientX();
		currentTouchPosition = startTouchPosition;
		startTouchTime = new Date().getTime();
		touchMoveHandler = touchPanel.addTouchMoveHandler(this);
		touchEndHandler = touchPanel.addTouchEndHandler(this);
	}


	@Override
	public void onOrientationChange()
	{
		if (getWidgetCount() > 0)
		{
			boolean hasNextPanel = hasNextWidget();
			boolean hasPreviousPanel = hasPreviousWidget();

			if (hasNextPanel || hasPreviousPanel)
			{
				Animation.translateX(getCurrentPanel(), 0, null);
				if (hasPreviousPanel)
				{
					Widget previousPanel = getPreviousPanel();
					Animation.translateX(previousPanel, -previousPanel.getOffsetWidth(), null);
				}
				if (hasNextPanel)
				{
					Widget nextPanel = getNextPanel();
					Animation.translateX(nextPanel, nextPanel.getOffsetWidth(), null);
				}
			}
		}
	}
	
	/**
	 * Sets the duration of the slide animations in milliseconds.
	 * @param slideTransitionDuration
	 */
	public void setSlideTransitionDuration(int transitionDuration) 
	{
		this.slideTransitionDuration = transitionDuration;
	}

	/**
	 * Gets the duration of the slide animations in milliseconds.
	 * @return
	 */
	public int getSlideTransitionDuration()
	{
		return slideTransitionDuration;
	}

	public void clear()
	{
		contentPanel.clear();
	}
	
	public void showWidget(int index)
	{
		//TODO fade animation
		setCurrentWidget(index);
	}
	
	public void showFirstWidget()
	{
		if (getWidgetCount() > 0)
		{
			showWidget(0);
		}
	}
	
	public void next()
	{
		if (currentWidget < 0)
		{
			showFirstWidget();
		}
		else if (hasNextWidget())
		{
			slide(-contentPanel.getElement().getOffsetWidth(), true);
		}
	}

	public void previous()
	{
		if (currentWidget < 0)
		{
			showFirstWidget();
		}
		else if (hasPreviousWidget())
		{
			slide(contentPanel.getElement().getOffsetWidth(), true);
		}
	}

	/**
	 * 
	 * @param slideBy
	 */
	private void slide(final int slideBy, boolean fireSlidingStartEvent)
	{
		isSliding = true;
		if (fireSlidingStartEvent)
		{
			SlidingEvent.fire(this, true);
		}
		Animation.translateX(getCurrentPanel(), slideBy, slideTransitionDuration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				int nextIndex = getNextIndexAfterSlide(slideBy);
				isSliding = false;
				setCurrentWidget(nextIndex);
				SlidingEvent.fire(TouchSlider.this, false);
			}
		});
		if (hasPreviousWidget())
		{
			Widget previousPanel = getPreviousPanel();
			Animation.translateX(previousPanel, slideBy-previousPanel.getOffsetWidth(), slideTransitionDuration, null);
		}
		if (hasNextWidget())
		{
			Widget nextPanel = getNextPanel();
			Animation.translateX(nextPanel, slideBy+nextPanel.getOffsetWidth(), slideTransitionDuration, null);
		}
	}

	/**
	 * return the final width used to slide the panels.
	 * @param hasPreviousPanel
	 * @param hasNextPanel
	 * @return negative width means "go to next", positive "go to previous" and zero "keep on current"
	 */
	private int getSlideBy()
	{
		int slideBy;
		int distX = currentTouchPosition - startTouchPosition;
		int width = contentPanel.getElement().getOffsetWidth();

		if (isSwapEvent(distX))
		{
			slideBy = distX > 0?width:-width;
		}
		else
		{
			if (Math.abs(distX) > width / 2)
			{
				slideBy = (distX > 0) ? width : width * -1;
			}
			else
			{
				slideBy = 0;
			}
		}

		if ((slideBy > 0 && !hasPreviousWidget()) || (slideBy < 0 && !hasNextWidget()))
		{
			slideBy = 0;
		}

		return slideBy;
	}

	private int getNextIndexAfterSlide(final int slideBy)
    {
        int index = currentWidget + (slideBy==0?0:slideBy<0?1:-1);
        if (circularShowing)
        {
        	int widgetCount = getWidgetCount();
			if (index >= widgetCount)
        	{
        		index = 0;
        	}
        	else if (index < 0)
        	{
        		index = widgetCount -1;
        	}
        }
		return index;
    }

	private boolean hasNextWidget()
	{
		return circularShowing || (currentWidget < getWidgetCount()-1);
	}

	private boolean hasPreviousWidget()
	{
		return circularShowing || (currentWidget > 0);
	}

	private Widget getCurrentPanel()
	{
		return contentPanel.getWidget(currentWidget);
	}

	private Widget getNextPanel()
	{
		int widgetCount = getWidgetCount();
		if (widgetCount > 0)
		{
			int index = currentWidget+1; 
			if (index >= widgetCount)
			{
				if (circularShowing)
				{
					index = 0;
				}
				else
				{
					return null;
				}
			}
			return contentPanel.getWidget(index);
		}
		return null;
	}

	private Widget getPreviousPanel()
	{
		int widgetCount = getWidgetCount();
		if (widgetCount > 0)
		{
			int index = currentWidget-1;
			if (index < 0)
			{
				if (circularShowing)
				{
					index = widgetCount-1;
				}
				else
				{
					return null;
				}
			}
			return contentPanel.getWidget(index);
		}
		return null;
	}

	private boolean isSwapEvent(int distX)
	{
		long endTime = new Date().getTime();
		long diffTime = endTime - this.startTouchTime;

		if (diffTime <= SWIPE_TIME_THRESHOLD)
		{
			if (Math.abs(distX) >= SWIPE_THRESHOLD)
			{
				return true;
			}
		}
		return false;
	}

	private void configurePanels()
	{
		if (currentWidget >=0 && currentWidget < getWidgetCount())
		{
			configureCurrentPanel();
			if (hasPreviousWidget())
			{
				configurePreviousPanel();
			}
			if (hasNextWidget())
			{
				configureNextPanel();
			}
		}
	}

	private void configureCurrentPanel() 
	{
		Widget currentPanel = getCurrentPanel();
		Animation.resetTransition(currentPanel);
		currentPanel.setVisible(true);
		//-webkit-backface-visibility: hidden;

	}

	private void configureHiddenPanel(Widget panel, boolean forward) 
	{
		panel.setVisible(true);
		int width = panel.getOffsetWidth();
		Animation.translateX(panel, forward?width:-width, null);
		//-webkit-backface-visibility: hidden;
	}

	private void configureNextPanel() 
	{
		configureHiddenPanel(getNextPanel(), true);
	}

	private void configurePreviousPanel() 
	{
		configureHiddenPanel(getPreviousPanel(), false);
	}

	/**
	 * Sets the widget that will be visible on this panel. 
	 * @param index
	 */
	private void setCurrentWidget(final int index) 
	{
		assert(index >=0 && index < getWidgetCount()):"Invalid index";//TODO message
		if (currentWidget != index)
		{
			this.currentWidget = index;
			configurePanels();
			SwapEvent.fire(this);
		}
	}
}
