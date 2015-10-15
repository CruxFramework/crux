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
package org.cruxframework.crux.core.client.screen;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RootViewContainer extends SingleViewContainer
{
	private Panel containerPanel;
	private View rootView;

	public RootViewContainer()
    {
		super(null, true);
		handleRootViewElementID();
		bindToDOM();
    }

	@Override
    @SuppressWarnings("deprecation")
	public com.google.gwt.user.client.Element getElement()
	{
	    return containerPanel.getElement();
	}

	public View getView()
	{
		return rootView;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
	    assert(views.isEmpty()):"RootViewContainer can not contain more then one view";
	    rootView = view;
	    boolean added = super.doAdd(view, lazy, parameter);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	rootView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	rootView = null;
	    }
		return removed;
	}
	
	protected Panel getContainerPanel()
    {
	    return containerPanel;
    }
	
	@Override
    protected Panel getContainerPanel(View view)
    {
	    return getContainerPanel();
    }

    @Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		Window.setTitle(title);
	}
	
	private void handleRootViewElementID()
	{
		String rootViewElementId = Crux.getConfig().rootViewElementId();
		if(!StringUtils.isEmpty(rootViewElementId))
		{
			containerPanel = RootPanel.get(rootViewElementId);
		}
		else
		{
			containerPanel = RootPanel.get();			
		}
		if(containerPanel != null)
		{
			containerPanel.clear();
		}
	}
}
