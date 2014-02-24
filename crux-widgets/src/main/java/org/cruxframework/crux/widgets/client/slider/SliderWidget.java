package org.cruxframework.crux.widgets.client.slider;

import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.widgets.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.paging.Pageable;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author samuel@cruxframework.org
 * A widget slider that can swap between widgets.
 */
interface SliderWidget extends IsWidget, HasSwapHandlers, OrientationChangeHandler, Pageable
{
	public ContentProvider getContentProvider();
	public void setContentProvider(ContentProvider contentProvider);
	
	public void add(Widget widget);
	public boolean remove(int index);
	public boolean remove(Widget widget);
	public void clear();
	
	public boolean isCircularShowing();
	public void setCircularShowing(boolean circularShowing);

	public int getWidgetCount();
	public Widget getWidget(int index);
	public int getWidgetIndex(Widget widget);
	public int getCurrentWidgetIndex();
	public Widget getCurrentWidget();
	
	public void setSlideTransitionDuration(int transitionDuration);
	public int getSlideTransitionDuration();
	
	public void showWidget(int index);
	public void showFirstWidget();
	public void next();
	public void previous();
	
	public HandlerRegistration addSwapHandler(SwapHandler handler);
}