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
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Gesse Dafe
 */
@Controller("menuTabsDisposalSmallController")
public class MenuTabsDisposalSmallController extends AbstractMenuTabsDisposalController
{
	private HorizontalSwapPanel swapPanel;
	private SimpleViewContainer viewContainer;
	
	@Override
	protected void init()
	{
		viewContainer = new SimpleViewContainer();
		swapPanel = getChildWidget("swapPanel");
		setStyleName("crux-MenuTabsDisposal");
		init((FlowPanel) getChildWidget("menuTabsHeader"), (FlowPanel)  getChildWidget("menuPanel"));
	}

	@Override
	@Expose
	public void showMenu()
	{
		if(swapPanel.getCurrentWidget().equals(getMenuPanel()) && getLastVisitedView() != null)
		{
			showView(getLastVisitedView(), false, Direction.FORWARD);
		}
		else
		{
			swapPanel.transitTo(getMenuPanel(), Direction.BACKWARDS);
		}
	}

	@Override
	public String getCurrentView() 
	{
		View activeView = viewContainer.getActiveView();
		
		if(activeView != null)
		{
			return activeView.getId();
		}
		
		return null;
	}

	@Override
	protected void doShowView(String targetView, Direction direction)
	{
		viewContainer.showView(targetView);
		swapPanel.transitTo(viewContainer, direction);
	}
	
	@Override
	protected void showView(String targetView, boolean saveHistory, Direction direction)
	{
		lastVisitedView = targetView;
		doShowView(targetView, direction);
	}
}
