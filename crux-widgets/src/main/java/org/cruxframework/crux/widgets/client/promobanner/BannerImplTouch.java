package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.slider.SlidingEvent;
import org.cruxframework.crux.widgets.client.slider.SlidingHandler;
import org.cruxframework.crux.widgets.client.slider.TouchSlider;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * PromoBanner implementation for touch devices
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class BannerImplTouch extends BannerImpl
{
	protected TouchSlider slider;
	
	
	protected BannerImplTouch()
	{
		super();
		slider = new TouchSlider();
		banners.add(slider);

		slider.setHeight("100%");
		slider.setWidth("100%");
		slider.setCircularShowing(true);
		slider.addSwapHandler(new SwapHandler()
		{
			@Override
			public void onSwap(SwapEvent event)
			{
				switchActiveBullet(slider.getCurrentWidget());
			}
		});
		slider.addSlidingHandler(new SlidingHandler()
		{
			@Override
			public void onSliding(SlidingEvent event)
			{
				autoTransitionBlocked = event.isMovementStarted();
				autoTransiteTimer.reschedule();
			}
		});
	}
	
	@Override
	public void doAddBanner(SimplePanel panel)
	{
		slider.add(panel);
	}
	
	@Override
	public int getNextBanner()
	{
		return slider.getCurrentWidget() + 1;
	}
	
	@Override
	public int getPreviousBanner()
	{
		return slider.getCurrentWidget() - 1;
	}
	
	@Override
	public boolean hasVisibleBanner()
	{
	    return slider.getCurrentWidget() >= 0;
	}
	
	@Override
	protected void nextBanner()
	{
	    slider.next();
		switchActiveBullet(slider.getCurrentWidget());
	}
	
	@Override
	protected void previousBanner()
	{
	    slider.previous();
		switchActiveBullet(slider.getCurrentWidget());
	}
	
	@Override
	public void showWidget(int i)
	{
		assert(i >= 0 && i < slider.getWidgetCount()) : "Invalid index";
		slider.showWidget(i);
		switchActiveBullet(i);
	}

	@Override
	public void showWidget(int i, boolean slideToRight)
	{
		assert(i >= 0 && i < slider.getWidgetCount()) : "Invalid index";
		slider.showWidget(i);
		switchActiveBullet(i);
	}

	@Override
	public int getBannersCount()
	{
		return slider.getWidgetCount();			
	}
	
	@Override
	public void setTransitionDuration(int transitionDuration)
	{
		this.slider.setSlideTransitionDuration(transitionDuration);
	}

	@Override
	public int getTransitionDuration()
	{
		return slider.getSlideTransitionDuration();
	}
	
	@Override
	protected void setBannersHeight(String height)
	{
		super.setBannersHeight(height);
		slider.setHeight(height);
	}
}
