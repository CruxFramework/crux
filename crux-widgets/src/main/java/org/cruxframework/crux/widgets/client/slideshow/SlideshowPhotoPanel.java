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
package org.cruxframework.crux.widgets.client.slideshow;

import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.event.tap.TapEvent;
import org.cruxframework.crux.widgets.client.event.tap.TapHandler;
import org.cruxframework.crux.widgets.client.slider.ContentProvider;
import org.cruxframework.crux.widgets.client.slider.SlidingEvent;
import org.cruxframework.crux.widgets.client.slider.SlidingHandler;
import org.cruxframework.crux.widgets.client.slider.TouchSlider;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowPhotoPanel extends SlideshowComponent 
{
	private SlideshowComponent impl;

	/**
	 * Marker interface for swapPhotoPanel rebind
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static interface SlideshowPhotoPanelImpl
	{
	}

	/**
	 * Implementation for desktops and no touch devices.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class SlideshowSwapPhotoPanel extends SlideshowComponent implements SlideshowPhotoPanelImpl 
	{
		private HorizontalSwapPanel photosPanel ;
		private FlowPanel mainPanel;
		private Label rightArrow;
		private Label leftArrow;
		
		@Override
	    public void onAlbumLoaded()
	    {
			photosPanel.clear();
	    }

		@Override
	    public void onPhotoLoaded(int previousIndex, int nextIndex)
	    {
			assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
			Direction direction = (nextIndex > previousIndex)?Direction.FORWARD:Direction.BACKWARDS;
			photosPanel.transitTo(getSlideshow().loadImage(nextIndex), direction);

			if (previousIndex == 0 && nextIndex > 0)
			{
				leftArrow.removeStyleDependentName("disabled");
			}
			else if (nextIndex == 0)
			{
				leftArrow.addStyleDependentName("disabled");
			}

			int lastIndex = getSlideshow().getPhotoCount() -1;
			if (previousIndex == lastIndex && nextIndex < lastIndex)
			{
				rightArrow.removeStyleDependentName("disabled");
			}
			else if (nextIndex == lastIndex)
			{
				rightArrow.addStyleDependentName("disabled");
			}
	    }

		@Override
	    public Widget createMainWidget()
	    {
			mainPanel = new FlowPanel();
			mainPanel.getElement().getStyle().setPosition(Position.RELATIVE);
			mainPanel.setWidth("100%");
			
			photosPanel = new HorizontalSwapPanel();
			photosPanel.setStyleName("crux-Photo");
			
			photosPanel.setWidth("100%");
			//see @ note1.
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					photosPanel.setHeight("100%");		
				}
			});
			
			leftArrow = new Label();
			leftArrow.setStyleName("leftArrow");
			leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
			leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
			leftArrow.getElement().getStyle().setTop(50, Unit.PCT);
			leftArrow.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getSlideshow().stop();
					getSlideshow().previous();
				}
			});
			
			rightArrow = new Label();
			rightArrow.setStyleName("rightArrow");
			rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
			rightArrow.getElement().getStyle().setRight(0, Unit.PX);
			rightArrow.getElement().getStyle().setTop(50, Unit.PCT);
			rightArrow.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getSlideshow().stop();
					getSlideshow().next();
				}
			});
			
			mainPanel.add(photosPanel);
			mainPanel.add(leftArrow);
			mainPanel.add(rightArrow);

			return mainPanel;
	    }
	}	
	
	/**
	 * Implementation for mobiles and touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class SlideshowTouchPhotoPanel extends SlideshowComponent implements SlideshowPhotoPanelImpl
	{
		private TouchSlider photosPanel ;
		
		@Override
	    public void onAlbumLoaded()
	    {
			photosPanel.setContentProvider(new ContentProvider()
			{
				@Override
				public int size()
				{
					return getSlideshow().getPhotoCount();
				}
				
				@Override
				public Widget loadWidget(int index)
				{
					return getSlideshow().loadImage(index);
				}
			});
	    }

		@Override
	    public void onPhotoLoaded(int previousIndex, int nextIndex)
	    {
			assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
			
			if (nextIndex == previousIndex + 1)
			{
				photosPanel.next();
			}
			else if (nextIndex == previousIndex - 1)
			{
				photosPanel.previous();
			}
			else if (photosPanel.getCurrentWidget() != nextIndex)
			{
				photosPanel.showWidget(nextIndex);
			}
	    }

		@Override
	    protected Widget createMainWidget()
	    {
			photosPanel = new TouchSlider();
			photosPanel.setWidth("100%");
			//see @ note1.
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					photosPanel.setHeight("100%");		
				}
			});
			
			photosPanel.addSwapHandler(new SwapHandler()
			{
				@Override
				public void onSwap(SwapEvent event)
				{
					getSlideshow().showPhoto(photosPanel.getCurrentWidget());
				}
			});
			photosPanel.addSlidingHandler(new SlidingHandler()
			{
				@Override
				public void onSliding(SlidingEvent event)
				{
					getSlideshow().stop();
				}
			});
			photosPanel.addTapHandler(new TapHandler()
			{
				@Override
				public void onTap(TapEvent event)
				{
					((SlideshowBaseController)getSlideshow()).showComponents();
				}
			});
			return photosPanel;
	    }
	}
	
	@Override
    protected Widget createMainWidget()
    {
		impl = GWT.create(SlideshowPhotoPanelImpl.class);
		impl.setStyleName("crux-SlideshowPhotoPanel");
		//note1: fixing height 'bug' that makes the object's height relative to parent. If parent is created
		//before then the object will not have the height setted with parent's value.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				impl.setHeight("100%");		
			}
		});
	    return impl;
    }
	
	@Override
	protected void onAlbumLoaded()
	{
	    impl.onAlbumLoaded();
	}
	
	@Override
	protected void onPhotoLoaded(int previousIndex, int nextIndex)
	{
		impl.onPhotoLoaded(previousIndex, nextIndex);
	}
	
	@Override
	public void setSlideShow(Slideshow slideshow)
	{
	    super.setSlideShow(slideshow);
	    impl.setSlideShow(slideshow);
	}
}
