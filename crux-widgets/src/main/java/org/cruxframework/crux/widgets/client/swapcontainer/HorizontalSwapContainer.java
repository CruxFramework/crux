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
package org.cruxframework.crux.widgets.client.swapcontainer;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.ChangeViewEvent;
import org.cruxframework.crux.core.client.screen.views.ChangeViewHandler;
import org.cruxframework.crux.core.client.screen.views.HasChangeViewHandlers;
import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HorizontalSwapContainer extends SingleViewContainer implements HasChangeViewHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-HorizontalSwapContainer";
	private HorizontalSwapPanel swapPanel;
	private Panel active;
	private Panel swap;
	private boolean autoRemoveInactiveViews = false;
	private boolean animationEnabled = true;
	
	public HorizontalSwapContainer()
	{
		super(new HorizontalSwapPanel(), false);
		swapPanel = getMainWidget();
		swapPanel.setStyleName(DEFAULT_STYLE_NAME);
		active = new SimplePanel();
//		active.setWidth("inherit");
//		active.setHeight("inherit");
		swap = new SimplePanel();
//		swap.setWidth("inherit");
//		swap.setHeight("inherit");
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAutoRemoveInactiveViews()
    {
    	return autoRemoveInactiveViews;
    }

	/**
	 * 
	 * @param autoRemoveInactiveViews
	 */
	public void setAutoRemoveInactiveViews(boolean autoRemoveInactiveViews)
    {
    	this.autoRemoveInactiveViews = autoRemoveInactiveViews;
    }

	/**
	 * 
	 * @param viewId
	 * @param direction
	 */
	public void showView(String viewName, Direction direction)
	{
		showView(viewName, viewName, direction);
	}

	/**
	 * 
	 * @param viewName
	 * @param viewId
	 * @param direction
	 */
	public void showView(String viewName, final String viewId, final Direction direction)
	{
		showView(viewName, viewId, direction, null);
	}
	
	/**
	 * 
	 * @param viewName
	 * @param viewId
	 * @param direction
	 */
	public void showView(String viewName, final String viewId, final Direction direction, final Object parameter)
	{
		if (views.containsKey(viewId))
		{
			renderView(getView(viewId), direction, parameter);
		}
		else
		{
			createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					if (addView(view, false, parameter))
					{
						renderView(view, direction, parameter);
					}
					else
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
					}
				}
			});
		}
	}

	/**
	 * 
	 * @param view
	 * @param direction
	 * @param parameter
	 */
	protected boolean renderView(View view, Direction direction, Object parameter)
	{
		if (activeView == null || !activeView.getId().equals(view.getId()))
		{
			final View previous = activeView;
			final View next = view;
			boolean rendered = super.renderView(view, parameter);
			if (rendered)
			{
				if (previous == null || direction == null || !animationEnabled)
				{
					swapPanel.setCurrentWidget(active);
					concludeViewsSwapping(previous, next);
				}
				else
				{
					swapPanel.transitTo(active, direction, new Callback()
					{
						@Override
						public void onTransitionCompleted()
						{
							concludeViewsSwapping(previous, next);
						}
					});
				}
			}
			return rendered;
		}
		return false;
	}

	private void concludeViewsSwapping(final View previous, final View next)
    {
	    swap.clear();
	    ChangeViewEvent.fire(HorizontalSwapContainer.this, previous, next);
	    if(previous != null && autoRemoveInactiveViews)
	    {
	    	previous.removeFromContainer();
	    }
    }

	@Override
	protected boolean renderView(View view, Object parameter)
	{
		return renderView(view, Direction.FORWARD, parameter);
	}

	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
		boolean activated = super.activate(view, containerPanel, parameter);
		if (activated)
		{
			swapPanelVariables();
		}
		return activated;
	}

	@Override
	protected Panel getContainerPanel(View view)
	{
		assert(view != null):"Can not retrieve a container for a null view";
		if (activeView != null && activeView.getId().equals(view.getId()))
		{
			return active;
		}
		return swap;
	}

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		// Do nothing
	}

	private void swapPanelVariables()
	{
		Panel temp = active;
		active = swap;
		swap = temp;
	}

	/**
	 * 
	 * @return
	 */
	public int getTransitionDuration()
	{
		return swapPanel.getTransitionDuration();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * 
	 * @param enabled
	 * @param device
	 */
	public void setAnimationEnabled(boolean enabled, Device device)
	{
		if (device == Device.all || Screen.getCurrentDevice() == device)
		{
			animationEnabled = enabled;
		}
	}
	
	/**
	 * 
	 * @param enabled
	 * @param size
	 */
	public void setAnimationEnabled(boolean enabled, Size size)
	{
		if (Screen.getCurrentDevice().getSize() == size)
		{
			animationEnabled = enabled;
		}
	}

	/**
	 * 
	 * @param enabled
	 */
	public void setAnimationEnabledForLargeDevices(boolean enabled)
	{
		setAnimationEnabled(enabled, Size.large);
	}
	
	/**
	 * 
	 * @param enabled
	 */
	public void setAnimationEnabledForSmallDevices(boolean enabled)
	{
		setAnimationEnabled(enabled, Size.small);
	}

	/**
	 * 
	 * @param transitionDuration
	 */
	public void setTransitionDuration(int transitionDuration)
	{
		swapPanel.setTransitionDuration(transitionDuration);
	}

	@Override
	public HandlerRegistration addChangeViewHandler(ChangeViewHandler handler)
	{
		return addHandler(handler, ChangeViewEvent.getType());
	}
}
