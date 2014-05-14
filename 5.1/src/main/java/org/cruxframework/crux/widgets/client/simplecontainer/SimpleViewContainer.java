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
package org.cruxframework.crux.widgets.client.simplecontainer;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SimpleViewContainer extends SingleViewContainer 
{
	public static final String DEFAULT_STYLE_NAME = "crux-SimpleViewContainer";
	private SimplePanel containerPanel;
	private View innerView;
	
	public SimpleViewContainer()
	{
		super(new SimplePanel(), true);
		containerPanel = getMainWidget();
		containerPanel.setStyleName(DEFAULT_STYLE_NAME);
	}

	public View getView()
	{
		return innerView;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
	    assert(views.isEmpty()):"SimpleViewContainer can not contain more then one view";
	    innerView = view;
	    boolean added = super.doAdd(view, lazy, parameter);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	innerView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	innerView = null;
	    }
		return removed;
	}
	
	@Override
    protected Panel getContainerPanel(View view)
    {
	    return getContainerPanel();
    }

    protected Panel getContainerPanel()
    {
	    return containerPanel;
    }
	
	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
	}
	
	@Override
	public void showView(String viewName, String viewId, Object parameter)
	{
	    if (getView() != null)
	    {
	    	if (getView().removeFromContainer())
	    	{
		    	super.showView(viewName, viewId, parameter);
	    	}
	    }
	    else
	    {
	    	super.showView(viewName, viewId, parameter);
	    }
	}
}
