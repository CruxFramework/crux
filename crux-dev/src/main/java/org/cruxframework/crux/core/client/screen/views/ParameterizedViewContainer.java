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
package org.cruxframework.crux.core.client.screen.views;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for ViewContainers that supports parameters on Views loading and activating.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ParameterizedViewContainer extends ViewContainer
{
	/**
	 * Constructor
	 * @param mainWidget main widget on this container
	 */
	public ParameterizedViewContainer(Widget mainWidget)
    {
	    super(mainWidget);
    }

	/**
	 * Constructor
	 * @param mainWidget Main widget on this container
	 * @param clearPanelsForDeactivatedViews If true, makes the container clear the container panel for a view, when the view is deactivated.
	 */
	public ParameterizedViewContainer(Widget mainWidget, boolean clearPanelsForDeactivatedViews)
    {
		super(mainWidget, clearPanelsForDeactivatedViews);
    }

	@Override
    public boolean add(View view, boolean render, Object parameter)
    {
    	return super.add(view, render, parameter);
    }
    
    @Override
	public void showView(String viewName, String viewId, Object parameter)
	{
    	super.showView(viewName, viewId, parameter);
	}

	@Override
	public void loadView(final String viewName, final String viewId, boolean render, Object parameter)
	{
		super.loadView(viewName, viewId, render, parameter);
	}
}
