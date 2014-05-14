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
package org.cruxframework.crux.widgets.client.slideshow;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("slideshowTouchController")
public class SlideshowTouchController extends SlideshowBaseController
{
	private DialogBox dialog;
	private SimplePanel mainPanel;

	@Expose
	public void onTouchEndDialog(TouchEndEvent event)
	{
		if (dialog.isShowing())
		{
			dialog.hide();
		}
		event.preventDefault();
		event.stopPropagation();
	}

	@Expose
	public void onTouchStartDialog(TouchStartEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
	}

	@Expose
	public void onTouchMoveDialog(TouchMoveEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
	}

	@Override
	protected void init()
	{
	    super.init();
	    dialog = getChildWidget("dialog");
	    mainPanel = getChildWidget("mainPanel");
	    adjustTableSize();
	}

	@Override
	public void onOrientationChange()
	{
	    super.onOrientationChange();
	    adjustTableSize();
	}
	
	@Override
    public void configurePhotoPanel()
    {
		mainPanel.add(photoPanel);
		mainPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    }
	
	@Override
	protected void showComponents()
	{
	    if (components.size() > 1)
	    {
	    	dialog.show();
	    }
	}

	private void adjustTableSize()
    {
	    Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				table.setWidth(asWidget().getOffsetWidth()+"px");
				table.setHeight(asWidget().getOffsetHeight()+"px");
			}
		});
    }
}
