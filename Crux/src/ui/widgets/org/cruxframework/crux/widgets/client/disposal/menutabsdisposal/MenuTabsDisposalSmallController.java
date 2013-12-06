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
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Gesse Dafe
 */
@Controller("menuTabsDisposalSmallController")
public class MenuTabsDisposalSmallController extends DeviceAdaptiveController implements MenuTabsDisposal
{
	private static final String HISTORY_PREFIX = "menuTabsDisposal:";
	
	private HorizontalSwapPanel swapPanel;
	private FlowPanel menuPanel;
	private SimpleViewContainer viewContainer;
	
	@Override
	public void addMenuEntry(String label, final String targetView)
	{
		Button menuItem = new Button();
		menuItem.addStyleName("menuEntry");
		menuItem.setText(label);
		menuItem.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				showView(targetView, true, Direction.FORWARD);
			}
		});
		
		menuPanel.add(menuItem);
	}

	@Override
	protected void init()
	{
		viewContainer = new SimpleViewContainer();
		swapPanel = getChildWidget("swapPanel");
		menuPanel = getChildWidget("menuPanel");
		setStyleName("crux-MenuTabsDisposal");
		
		Screen.addHistoryChangedHandler(new ValueChangeHandler<String>() 
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event) 
			{
				String token = event.getValue();
				if(token != null && token.startsWith(HISTORY_PREFIX))
				{
					showView(token.replace(HISTORY_PREFIX, ""), false, Direction.BACKWARDS);
				}
			}
		});
	}

	@Override
	public void showMenu()
	{
		swapPanel.transitTo(menuPanel, Direction.BACKWARDS);
	}

	@Override
	public void addMenuSection(final String label, String additionalStyleName)
	{
		Label section = new Label();
		section.setStyleName("menuSection");
		section.getElement().getStyle().setDisplay(Display.BLOCK);
		section.setText(label);

		if(!StringUtils.isEmpty(additionalStyleName))
		{
			section.addStyleName(additionalStyleName);
		}
		
		menuPanel.add(section);
	}
	
	private void showView(final String targetView, boolean saveHistoryToken, Direction direction) 
	{
		if(saveHistoryToken)
		{
			History.newItem(HISTORY_PREFIX + targetView);
		}
		else
		{
			viewContainer.showView(targetView);
			swapPanel.transitTo(viewContainer, direction);
		}
	}

	@Override
	public void setHeaderContent(IsWidget widget) 
	{
		// TODO Auto-generated method stub
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
}
