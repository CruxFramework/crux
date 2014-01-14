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
package org.cruxframework.crux.widgets.client.disposal.menutabsdisposal;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;
import org.cruxframework.crux.widgets.client.tabcontainer.TabContainer;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Gesse Dafe
 */
@Controller("menuTabsDisposalLargeController")
public class MenuTabsDisposalLargeController extends AbstractMenuTabsDisposalController
{
	private TabContainer viewContainer;
	
	@Override
	protected void init()
	{
		setStyleName("crux-MenuTabsDisposal");
		viewContainer = getChildWidget("viewContainer");
		init((FlowPanel) getChildWidget("menuTabsHeader"), (FlowPanel)  getChildWidget("menuPanel"));
	}		

	@Override
	public void showMenu()
	{
		int index = viewContainer.getFocusedViewIndex();
		if(index >= 0)
		{
			String viewId = viewContainer.getViewId(index);
			viewContainer.closeView(viewId, true);
		}
	}

	@Override
	public String getCurrentView() 
	{
		int focusedViewIndex = viewContainer.getFocusedViewIndex();
		
		if(focusedViewIndex >= 0)
		{
			return viewContainer.getViewId(focusedViewIndex);
		}
		
		return null;
	}

	@Override
	protected void doShowView(String targetView, Direction direction)
	{
		viewContainer.showView(targetView, targetView);
		viewContainer.focusView(targetView);
	}
}
