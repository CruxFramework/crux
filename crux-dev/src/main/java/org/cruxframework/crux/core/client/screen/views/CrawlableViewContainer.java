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

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view container that handle history changes using the hashbang approach
 * {@link https://developers.google.com/webmasters/ajax-crawling/docs/getting-started}
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class CrawlableViewContainer extends ViewContainer
{
	private boolean historyControlEnabled = true;
	private String historyControlPrefix = null;
	private HandlerRegistration historyControlHandler;
	private boolean createNewHistoryToken = true;

	/**
	 * Constructor
	 * @param mainWidget main widget on this container
	 */
	public CrawlableViewContainer(Widget mainWidget)
    {
	    this(mainWidget, true);
    }

	/**
	 * Constructor
	 * @param mainWidget Main widget on this container
	 * @param clearPanelsForDeactivatedViews If true, makes the container clear the container panel for a view, when the view is deactivated.
	 */
	public CrawlableViewContainer(Widget mainWidget, boolean clearPanelsForDeactivatedViews)
    {
		super(mainWidget, clearPanelsForDeactivatedViews);
    }
	
	/**
	 * Retrieve the historyControlEnabled property value. When this property is enabled, the container control 
	 * automatically the browser history to forward and back between container's views changes 
	 * @return true if enabled
	 */
	public boolean isHistoryControlEnabled() 
	{
		return historyControlEnabled;
	}

	/**
	 * Sets the historyControlEnabled property value. When this property is enabled, the container control 
	 * automatically the browser history to forward and back between container's views changes 
	 * @param historyControlEnabled true to enable history control
	 */
	public void setHistoryControlEnabled(boolean historyControlEnabled) 
	{
		this.historyControlEnabled = historyControlEnabled;
	}

	/**
	 * Retrieve the historyControlPrefix property value. When historyControlEnabled property is enabled, the 
	 * container control automatically the browser history to forward and back between container's views changes.
	 * This property inform the prefix that will be used on historyTokens created by this container 
	 * 
	 * @return history prefix 
	 */
	public String getHistoryControlPrefix()
	{
		return historyControlPrefix;
	}

	/**
	 * Sets the historyControlPrefix property value. When historyControlEnabled property is enabled, the 
	 * container control automatically the browser history to forward and back between container's views changes.
	 * This property inform the prefix that will be used on historyTokens created by this container 
	 * 
	 * @param historyControlPrefix history prefix to set
	 */
	public void setHistoryControlPrefix(String historyControlPrefix)
	{
		if (!StringUtils.isEmpty(historyControlPrefix))
		{
			if (!historyControlPrefix.startsWith("!"))
			{
				historyControlPrefix = "!"+historyControlPrefix;
			}
			if (!historyControlPrefix.endsWith("="))
			{
				historyControlPrefix = historyControlPrefix+"=";
			}
		}
		else
		{
			historyControlPrefix = getDefaultHistoryPrefix();
		}
		this.historyControlPrefix = historyControlPrefix;
	}

	/**
	 * Return the default historyPrefix
	 * @return the default historyPrefix
	 */
	protected String getDefaultHistoryPrefix()
    {
	    return "!view=";
    }

	@Override
	protected void bindToDOM()
	{
	    super.bindToDOM();
	    attachHistoryControlHandler();
	    synchronizeHistoryState();
	}
	
	@Override
	protected void unbindToDOM()
	{
		dettachHistoryControlHandler();
	    super.unbindToDOM();
	}
	
	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
	    boolean activated = super.activate(view, containerPanel, parameter);
	    if (isHistoryControlEnabled() && activated)
	    {
	    	if (createNewHistoryToken)
	    	{
	    		Screen.addToHistory(this.historyControlPrefix+view.getId());
	    	}
	    	else
	    	{
	    		createNewHistoryToken = true;
	    	}
	    }
		return activated;
	}
	
	protected void attachHistoryControlHandler()
	{
		dettachHistoryControlHandler();
		historyControlHandler = Screen.addHistoryChangedHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				String token = event.getValue();
				updateViewToken(token);
			}
		});
	}
	
	protected void dettachHistoryControlHandler()
	{
		if (historyControlHandler != null)
		{
			historyControlHandler.removeHandler();
			historyControlHandler = null;
		}
	}

	protected void synchronizeHistoryState()
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				String historyItem = Screen.getCurrentHistoryItem();
				updateViewToken(historyItem);
			}
		});
	}
	
	protected void updateViewToken(String token)
	{
		if (isHistoryControlEnabled())
		{
			if (token != null && token.startsWith(historyControlPrefix))
			{
				String viewId = token.substring(historyControlPrefix.length());
				if (!isViewDisplayed(viewId))
				{
					createNewHistoryToken = false; 
					showView(viewId);
				}
			}
		}
	}
	
	protected abstract boolean isViewDisplayed(String viewId);
}
