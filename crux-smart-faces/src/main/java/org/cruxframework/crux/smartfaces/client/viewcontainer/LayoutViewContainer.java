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
package org.cruxframework.crux.smartfaces.client.viewcontainer;

import org.cruxframework.crux.core.client.screen.views.MultipleViewsContainer;
import org.cruxframework.crux.core.client.screen.views.View;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author wesley.diniz
 * 
 */
public class LayoutViewContainer extends MultipleViewsContainer
{

	private Panel containerPanel;
	private View lastView;

	public LayoutViewContainer()
	{
		super(new FlowPanel(), true);
		containerPanel = getMainWidget();
	}

	@Override
	protected Panel getContainerPanel(View view)
	{
		return containerPanel;
	}

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId){}

	/**
	 * Render the requested view into the container.
	 * 
	 * @param viewName
	 *            View name
	 */
	@Override
	public void showView(String viewName)
	{
		if (lastView != null)
		{
			deactivate(lastView, this.containerPanel, true);
		}

		showView(viewName, viewName);

		lastView = getView(viewName);
	}

}