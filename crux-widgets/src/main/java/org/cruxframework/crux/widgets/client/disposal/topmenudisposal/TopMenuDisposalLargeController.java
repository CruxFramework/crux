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
package org.cruxframework.crux.widgets.client.disposal.topmenudisposal;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author Gesse Dafe
 */
@Controller("topMenuDisposalLargeController")
public class TopMenuDisposalLargeController extends DeviceAdaptiveController implements TopMenuDisposal
{
	private static final String HISTORY_PREFIX = "topMenuDisposal:";
	
	private MenuBar menuBar;
	private SimpleViewContainer viewContainer;

	private String viewName;
	
	@Override
	public void addMenuEntry(final String label, final String targetView)
	{
		MenuItem menuItem = new MenuItem(new SafeHtmlBuilder().appendEscaped(label).toSafeHtml());
		menuItem.addStyleName("menuEntry");
		menuItem.setScheduledCommand(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				showView(targetView, true);
			}
		});
		menuBar.addItem(menuItem);
	}
	
	@Override
	protected void init()
	{
		menuBar = getChildWidget("menuBar");
		viewContainer = getChildWidget("viewContainer");
		setStyleName("crux-TopMenuDisposal");
		
		Screen.addHistoryChangedHandler(new ValueChangeHandler<String>() 
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event) 
			{
				String token = event.getValue();
				if(token != null && token.startsWith(HISTORY_PREFIX))
				{
					showView(token.replace(HISTORY_PREFIX, ""), false);
				}
			}
		});
		
		//Favorites
		String hash = com.google.gwt.user.client.Window.Location.getHash();
		if(hash != null && hash.startsWith(HASH + HISTORY_PREFIX))
		{
			String currentViewName = hash.replace(HASH + HISTORY_PREFIX, "");
			showView(currentViewName, false);
			History.newItem(HISTORY_PREFIX + currentViewName);
		}
		
		//DefaultView
		if(viewName != null)
		{
			showView(viewName.replace(HISTORY_PREFIX, ""), false);
			History.newItem(HISTORY_PREFIX + viewName);
		}
	}

	@Override
	public void showMenu()
	{
		// nothing to do
	}

	@Override
	public void showView(String viewName, boolean saveHistory) 
	{
		if(saveHistory)
		{
			History.newItem(HISTORY_PREFIX + viewName);
		}
		else
		{
			viewContainer.showView(viewName);
			Window.scrollTo(0, 0);
		}
	}

	@Override
	public void setDefaultView(String viewName) 
	{
		this.viewName = viewName;
	}
}
