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
package org.cruxframework.crux.widgets.client.disposal.panelchoicedisposal;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;
import org.cruxframework.crux.widgets.client.swapcontainer.SwapContainer;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Gesse Dafe
 */
@Controller("panelChoiceDisposalSmallController")
public class PanelChoiceDisposalSmallController extends DeviceAdaptiveController implements PanelChoiceDisposal
{
	private SwapContainer viewContainer;
	private ListBox viewSelector;
	
	@Override
	protected void init()
	{
		setStyleName("crux-PanelChoiceDisposal");
		this.viewContainer = getChildWidget("viewContainer");
		this.viewSelector = getChildWidget("viewSelector");
	}
	
	
	@Override
	public void addChoice(String id, String label, String targetView, ViewActivateHandler handler)
	{
		viewContainer.showView(targetView, id);
		View view = viewContainer.getActiveView();
		view.addViewActivateHandler(handler);
		viewSelector.addItem(label, id);
	}
	
	@Expose
	public void selectView(ChangeEvent event)
	{
		int index = viewSelector.getSelectedIndex();
		String targetView = viewSelector.getValue(index);
		viewContainer.showView(targetView);
	}


	@Override
	public void choose(String targetView, String viewId)
	{
		viewContainer.showView(targetView, viewId);
		int viewCount = viewSelector.getItemCount();
		for(int i = 0; i < viewCount; i++)
		{
			String value = viewSelector.getValue(i);
			if(value != null && value.endsWith(viewId))
			{
				viewSelector.setSelectedIndex(i);
			}
		}
	}
}
