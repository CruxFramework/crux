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
package org.cruxframework.crux.widgets.client.dialog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows a progress message
 * @author Gesse Dafe
 */
public class ProgressBox extends AbstractDialogBox
{
	/**
	 * Creates a progress box
	 */
	public ProgressBox()
	{
		super(true, false, false);
		setStyleName("crux-ProgressBox");
		setWidget(createInfiniteProgressBar());
	}
	
	/**
	 * Sets the message to be shown
	 * @param message
	 */
	public void setMessage(String message)
	{
		super.setTitle(message);
	}
	
	/**
	 * Shows a progress dialog
	 * @param message the text to be displayed
	 */
	public static ProgressBox show(String message)
	{
		ProgressBox progressDialog = new ProgressBox(); 
		progressDialog.setMessage(message);
		progressDialog.show();
		progressDialog.center();
		return progressDialog;
	}

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createInfiniteProgressBar() 
	{
		FlowPanel bar = new FlowPanel();
		bar.setStyleName("crux-InfinityProgressBar");
		
		SimplePanel slot = new SimplePanel();
		slot.setStyleName("progressBarSlot");
		bar.add(slot);
		
		SimplePanel fill = new SimplePanel();
		fill.setStyleName("progressBarFill");
		bar.add(fill);

		return bar;
	}	
}