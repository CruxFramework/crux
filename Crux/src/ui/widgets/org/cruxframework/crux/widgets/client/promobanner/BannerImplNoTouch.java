package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * PromoBanner implementation for non touch devices
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class BannerImplNoTouch extends BannerImpl
{
	protected HorizontalSwapPanel swapPanel;
	protected Label leftArrow;
	protected Label rightArrow;
	protected int visibleBanner = -1;
	protected FastList<SimplePanel> panels = new FastList<SimplePanel>();
	protected boolean controlsEnabled = true;


	protected BannerImplNoTouch()
	{
		super();
		swapPanel = new HorizontalSwapPanel();
		swapPanel.setUseFadeTransitions(true);
		banners.add(swapPanel);

		swapPanel.setHeight("100%");
		swapPanel.setWidth("100%");

		focusPanel.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				if (controlsEnabled)
				{
					if(event.isLeftArrow())
					{
						showBanner(visibleBanner - 1, false);
					}
					else if(event.isRightArrow())
					{
						showBanner(visibleBanner + 1, true);
					}
				}
			}
		});

		leftArrow = new Label();
		leftArrow.setStyleName("leftArrow");
		leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		leftArrow.addClickHandler( new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if (controlsEnabled)
				{
					showBanner(visibleBanner - 1, false);
				}
			}
		});

		rightArrow = new Label();
		rightArrow.setStyleName("rightArrow");
		rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		rightArrow.addClickHandler( new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if (controlsEnabled)
				{
					showBanner(visibleBanner + 1, true);
				}
			}
		});


		banners.add(leftArrow);
		banners.add(rightArrow);

		Scheduler.get().scheduleDeferred(
				new ScheduledCommand()
				{
					public void execute()
					{
						adjustPositions();
					}
				}
		);
	}

	@Override
	public void doAddBanner(SimplePanel panel)
	{
		panels.add(panel);
	}

	@Override
	public int getNextBanner()
	{
		return visibleBanner + 1;
	}

	@Override
	public int getPreviousBanner()
	{
		return visibleBanner - 1;
	}

	@Override
	public boolean hasVisibleBanner()
	{
		return visibleBanner >= 0;
	}

	@Override
	public void showWidget(int i)
	{
		assert(i >= 0 && i < panels.size()) : "Invalid index";
		visibleBanner = i;
		swapPanel.setCurrentWidget(panels.get(i));
	}

	@Override
	public void showWidget(int i, boolean slideToRight)
	{
		assert(i >= 0 && i < panels.size()) : "Invalid index";
		visibleBanner = i;
		setControlsEnabled(false);
		swapPanel.transitTo(panels.get(i), slideToRight?Direction.FORWARD:Direction.BACKWARDS, new Callback()
		{

			@Override
			public void onTransitionCompleted()
			{
				setControlsEnabled(true);
			}
		});
	}

	protected void setControlsEnabled(boolean controlsEnabled)
	{
		this.controlsEnabled = controlsEnabled; 
		if (this.controlsEnabled)
		{
			StyleUtils.removeStyleDependentName(leftArrow.getElement(), "disabled");
			StyleUtils.removeStyleDependentName(rightArrow.getElement(), "disabled");
		}
		else
		{
			autoTransiteTimer.reschedule();
			StyleUtils.addStyleDependentName(leftArrow.getElement(), "disabled");
			StyleUtils.addStyleDependentName(rightArrow.getElement(), "disabled");
		}
	}

	@Override
	public int getBannersCount()
	{
		return panels.size();			
	}

	@Override
	public void setTransitionDuration(int transitionDuration)
	{
		this.swapPanel.setTransitionDuration(transitionDuration);
	}

	@Override
	public int getTransitionDuration()
	{
		return swapPanel.getTransitionDuration();
	}

	@Override
	protected void setBannersHeight(String height)
	{
		super.setBannersHeight(height);
		swapPanel.setHeight(height);
	}

	protected void adjustPositions()
	{
		int containerHeight = banners.getElement().getClientHeight();

		rightArrow.getElement().getStyle().setRight(0, Unit.PX);
		rightArrow.getElement().getStyle().setTop((containerHeight - rightArrow.getElement().getClientHeight())/2, Unit.PX);

		leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
		leftArrow.getElement().getStyle().setTop((containerHeight - leftArrow.getElement().getClientHeight())/2, Unit.PX);
	}
}
