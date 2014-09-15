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

import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.widgets.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;
import org.cruxframework.crux.widgets.client.paging.Pageable;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
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

	public void changeControlsVisibility(boolean visible);
	
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