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
package org.cruxframework.crux.widgets.client.slider;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.event.tap.HasTapHandlers;
import org.cruxframework.crux.widgets.client.event.tap.TapHandler;
import org.cruxframework.crux.widgets.client.paging.Pager;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * A crossdevice panel that swaps its contents using slide animations.
 */
public class Slider extends Composite implements SliderWidget
{
	private GeneratedSlider slider;
	
	public Slider()
	{
		slider = GWT.create(GeneratedSlider.class);
		slider.asWidget().setStyleName("crux-Slider");
		initWidget((Widget) slider);
	}
	
	/**
	 * public Slider interface responsible for creating the object
	 */
	public interface GeneratedSlider extends SliderWidget
	{
	}
	
	public static class InnerTouchSlider extends Composite implements GeneratedSlider, HasSlidingHandlers, HasTapHandlers 
	{
		private TouchSlider slider;
		private FlowPanel wrapper;
		
		public InnerTouchSlider() 
		{
			super();
			
			wrapper = new FlowPanel();
			slider = new TouchSlider();
			fixWidthHeight(slider);

			wrapper.add(slider);
			initWidget(wrapper);
		}

		@Override
		public void onOrientationChange() 
		{
			slider.onOrientationChange();
		}

		@Override
		public ContentProvider getContentProvider() 
		{
			return slider.getContentProvider();
		}

		@Override
		public void setContentProvider(ContentProvider contentProvider) 
		{
			slider.setContentProvider(contentProvider);	
		}

		@Override
		public void add(Widget widget) 
		{
			slider.add(widget);	
		}

		@Override
		public boolean isCircularShowing() 
		{
			return slider.isCircularShowing();
		}

		@Override
		public void setCircularShowing(boolean circularShowing) 
		{
			slider.setCircularShowing(circularShowing);			
		}

		@Override
		public HandlerRegistration addSwapHandler(SwapHandler handler) 
		{
			return slider.addSwapHandler(handler);
		}

		@Override
		public HandlerRegistration addSlidingHandler(SlidingHandler handler) 
		{
			return slider.addSlidingHandler(handler);
		}

		@Override
		public HandlerRegistration addTapHandler(TapHandler handler) 
		{
			return slider.addTapHandler(handler);
		}

		@Override
		public Widget getWidget(int index) 
		{
			return slider.getWidget(index);
		}

		@Override
		public int getWidgetCount() 
		{
			return slider.getWidgetCount();
		}

		@Override
		public int getWidgetIndex(Widget child) 
		{
			return slider.getWidgetIndex(child);
		}

		@Override
		public boolean remove(int index) 
		{
			return slider.remove(index);
		}

		@Override
		public Widget getCurrentWidget() 
		{
			return slider.getWidget(slider.getCurrentWidget());
		}

		@Override
		public void setSlideTransitionDuration(int transitionDuration) 
		{
			slider.setSlideTransitionDuration(transitionDuration);	
		}

		@Override
		public int getSlideTransitionDuration() 
		{
			return slider.getSlideTransitionDuration();
		}

		@Override
		public void clear() 
		{
			slider.clear();	
		}

		@Override
		public void showWidget(int index) 
		{
			slider.showWidget(index);	
		}

		@Override
		public void showFirstWidget() 
		{
			slider.showFirstWidget();	
		}

		@Override
		public void next() 
		{
			slider.next();	
		}

		@Override
		public void previous() 
		{
			slider.previous();	
		}

		@Override
		public boolean remove(Widget widget) 
		{
			return slider.remove(slider.getWidgetIndex(widget));
		}

		@Override
		public int getCurrentWidgetIndex() 
		{
			return slider.getCurrentWidget();
		}

		@Override
		public void nextPage() 
		{
			next();
		}

		@Override
		public void previousPage() 
		{
			previous();	
		}

		@Override
		public int getPageCount() 
		{
			return getWidgetCount();
		}

		@Override
		public void setPager(Pager pager) 
		{
			wrapper.add(pager);
			pager.setVisible(true);
			pager.setEnabled(true);
		}

		@Override
		public void goToPage(int page) 
		{
			showWidget(page);
		}

		@Override
		public boolean isDataLoaded() 
		{
			return true;
		}

		@Override
		public void changeControlsVisibility(boolean visible) 
		{
			//DONOTHING
		}
	}
	
	public static class InnerNoTouchSlider extends Composite implements GeneratedSlider
	{
		private FastList<Widget> widgets = new FastList<Widget>();
		private int currentIndex;
		
		private Pager pager;
		private FlowPanel wrapper;
		private HorizontalSwapPanel slider;
		
		private boolean circularShowing;
		private ContentProvider contentProvider;
		
		protected FlowPanel buttonWrapper;
		
		public InnerNoTouchSlider() 
		{
			super();
			wrapper = new FlowPanel();
//			wrapper.setWidth("100%");
		
			slider = new HorizontalSwapPanel();
			//fixWidthHeight(slider);

			Button leftButton = new Button();
			leftButton.setText("<");
			leftButton.setStyleName("leftArrow");
			leftButton.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					previous();
				}
			});

			Button rightButton = new Button();
			rightButton.setText(">");
			rightButton.setStyleName("rightArrow");
			rightButton.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					next();
				}
			});

			buttonWrapper = new FlowPanel();
			buttonWrapper.add(leftButton);
			buttonWrapper.add(rightButton);
			
			wrapper.add(slider);
			wrapper.add(buttonWrapper);
			
			slider.setUseFadeTransitions(true);
			
			initWidget(wrapper);
		}
		
		private void configureNextCurrentIndex(int index) 
		{
			if(index >= getWidgetCount())
			{
				if(isCircularShowing())
				{
					currentIndex = 0;
				} else
				{
					currentIndex = getWidgetCount()-1;
				}
			} else if(index < 0)
			{
				if(isCircularShowing())
				{
					currentIndex = getWidgetCount()-1;
				} else
				{
					currentIndex = 0;
				}
			} else 
			{
				currentIndex = index;
			}
		}
		
		@Override
		public ContentProvider getContentProvider() 
		{
			return contentProvider;
		}

		@Override
		public void setContentProvider(ContentProvider contentProvider) 
		{
			this.contentProvider = contentProvider;
		}

		@Override
		public void add(Widget widget) 
		{
			widgets.add(widget);
		}

		@Override
		public boolean isCircularShowing() 
		{
			return circularShowing;
		}

		@Override
		public void setCircularShowing(boolean circularShowing) 
		{
			this.circularShowing = circularShowing;			
		}

		@Override
		public HandlerRegistration addSwapHandler(SwapHandler handler) 
		{
			return slider.addSwapHandler(handler);
		}

		@Override
		public Widget getWidget(int index) 
		{
			return widgets.get(index);
		}

		@Override
		public int getWidgetCount() 
		{
			return widgets.size();
		}

		@Override
		public int getWidgetIndex(Widget child) 
		{
			return widgets.indexOf(child);
		}

		@Override
		public boolean remove(int removeIndex) 
		{
			if(removeIndex < 0)
			{
				return false;
			}
			
			if(widgets.remove(removeIndex) == null)
			{
				return false;
			}
			
			if(removeIndex == currentIndex)
			{
				configureNextCurrentIndex(++removeIndex);
				showWidget(currentIndex);
			}
			
			return true;
		}

		@Override
		public boolean remove(Widget widget) 
		{
			return remove(widgets.indexOf(widget)); 
		}
		
		@Override
		public Widget getCurrentWidget() 
		{
			return widgets.get(currentIndex);
		}

		@Override
		public void setSlideTransitionDuration(int transitionDuration) 
		{
			slider.setTransitionDuration(transitionDuration);
		}

		@Override
		public int getSlideTransitionDuration() 
		{
			return slider.getTransitionDuration();
		}

		@Override
		public void clear() 
		{
			widgets.clear();
			slider.clear();
			currentIndex = 0;
			showWidget(currentIndex);
		}

		@Override
		public void showWidget(final int index) 
		{
			slider.setCurrentWidget(contentProvider != null ? 
					contentProvider.loadWidget(index) : widgets.get(index));	
		}

		@Override
		public void showFirstWidget() 
		{
			currentIndex = 0;
			showWidget(0);
			if(pager != null)
			{
				pager.update(1, 1 == getWidgetCount());
			}
		}

		@Override
		public void next() 
		{
			configureNextCurrentIndex(++currentIndex);
			showWidget(currentIndex);
		}

		@Override
		public void previous() 
		{
			configureNextCurrentIndex(--currentIndex);
			showWidget(currentIndex);	
		}

		@Override
		public void onOrientationChange() 
		{
			//DO NOTHING
		}

		@Override
		public int getCurrentWidgetIndex() 
		{
			return currentIndex;
		}
		
		@Override
		public void nextPage() 
		{
			next();
			pager.update(getCurrentWidgetIndex()+1, ((getCurrentWidgetIndex()) == getWidgetCount()));
		}

		@Override
		public void previousPage() 
		{
			previous();
			pager.update(getCurrentWidgetIndex()+1, ((getCurrentWidgetIndex()) == getWidgetCount()));
		}

		@Override
		public int getPageCount() 
		{
			return getWidgetCount();
		}

		@Override
		public void setPager(Pager pager) 
		{
			this.pager = pager;
			wrapper.remove(buttonWrapper);
		}

		@Override
		public void goToPage(int page) 
		{
			showWidget(page-1);
			configureNextCurrentIndex(page-1);
			pager.update(page, ((page) == getWidgetCount()));
		}

		@Override
		public boolean isDataLoaded() 
		{
			return true;
		}

		@Override
		public void changeControlsVisibility(boolean visible) 
		{
			buttonWrapper.setVisible(visible);
		}
	}

	//note1: fixing height 'bug' that makes the object's height relative to parent. If parent is created
	//before then the object will not have the height setted with parent's value.
	@SuppressWarnings("unused")
	private static void fixWidthHeight(final Widget widget) 
	{
		widget.setWidth("100%");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				widget.setHeight("100%");
			}
		});
	}
			
	@Override
	public ContentProvider getContentProvider() 
	{
		return slider.getContentProvider();
	}

	@Override
	public void setContentProvider(ContentProvider contentProvider) 
	{
		slider.setContentProvider(contentProvider);
	}

	@Override
	public void add(Widget widget) 
	{
		slider.add(widget);
	}

	@Override
	public boolean isCircularShowing() 
	{
		return slider.isCircularShowing();
	}

	@Override
	public void setCircularShowing(boolean circularShowing) 
	{
		slider.setCircularShowing(circularShowing);			
	}

	@Override
	public HandlerRegistration addSwapHandler(SwapHandler handler) 
	{
		return slider.addSwapHandler(handler);
	}

	@Override
	public Widget getWidget(int index) 
	{
		return slider.getWidget(index);
	}

	@Override
	public int getWidgetCount() 
	{
		return slider.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget child) 
	{
		return slider.getWidgetIndex(child);
	}

	@Override
	public boolean remove(int index) 
	{
		return slider.remove(index);
	}

	@Override
	public Widget getCurrentWidget() 
	{
		return slider.getCurrentWidget();
	}

	@Override
	public void setSlideTransitionDuration(int transitionDuration) 
	{
		slider.setSlideTransitionDuration(transitionDuration);			
	}

	@Override
	public int getSlideTransitionDuration() 
	{
		return slider.getSlideTransitionDuration();
	}

	@Override
	public void clear() 
	{
		slider.clear();
	}

	@Override
	public void showWidget(int index) 
	{
		slider.showWidget(index);	
	}

	@Override
	public void showFirstWidget() 
	{
		slider.showFirstWidget();	
	}

	@Override
	public void next() 
	{
		slider.next();	
	}

	@Override
	public void previous() 
	{
		slider.previous();	
	}

	@Override
	public void onOrientationChange() 
	{
		slider.onOrientationChange();
	}

	@Override
	public Widget asWidget() 
	{
		return slider.asWidget();
	}

	@Override
	public void fireEvent(GwtEvent<?> event) 
	{
		slider.fireEvent(event);
	}

	@Override
	public boolean remove(Widget widget) 
	{
		return slider.remove(widget);
	}

	@Override
	public int getCurrentWidgetIndex() 
	{
		return slider.getCurrentWidgetIndex();
	}

	@Override
	public void nextPage() 
	{
		slider.nextPage();	
	}

	@Override
	public void previousPage() 
	{
		slider.previousPage();	
	}

	@Override
	public int getPageCount() 
	{
		return slider.getPageCount();
	}

	@Override
	public void setPager(Pager pager) 
	{
		slider.setPager(pager);	
	}

	@Override
	public void goToPage(int page) 
	{
		slider.goToPage(page);	
	}

	@Override
	public boolean isDataLoaded() 
	{
		return slider.isDataLoaded();
	}

	@Override
	public void changeControlsVisibility(boolean visible) 
	{
		slider.changeControlsVisibility(visible);
	}
}
