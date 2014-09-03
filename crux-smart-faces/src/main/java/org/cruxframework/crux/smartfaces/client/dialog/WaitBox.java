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
package org.cruxframework.crux.smartfaces.client.dialog;

import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows a wait widget and a message
 * @author Thiago da Rosa de Bustamante
 */
public class WaitBox extends AbstractDialogBox
{
	public static final String DEFAULT_STYLE_NAMES = "faces-WaitBox faces-popup";

	/**
	 * Creates a wait box
	 */
	public WaitBox()
	{
		this(true);
	}
	
	/**
	 * Creates a wait box
	 * @param movable
	 */
	public WaitBox(boolean movable)
	{
		super(movable, false, false, true, DEFAULT_STYLE_NAMES);
		setWidget(createProgressBar());
	}

	/**
	 * Sets the message to be shown
	 * @param message
	 */
	public void setMessage(String message)
	{
		super.setDialogTitle(message);
	}
	
	/**
	 * Shows a wait box
	 * @param message the text to be displayed
	 */
	public static WaitBox show(String message)
	{
		return show(message, null);
	}
	
	/**
	 * Shows a wait box
	 * @param message the text to be displayed
	 * @param animation animates the dialog while showing or hiding
	 */
	public static WaitBox show(String message, DialogAnimation animation)
	{
		WaitBox waitBox = new WaitBox();
		if (animation != null)
		{
			waitBox.setAnimation(animation);
		}
		waitBox.setMessage(message);
		waitBox.center();
		return waitBox;
	}

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createProgressBar() 
	{
		HTML bar = new HTML("<div class='faces-progressBar-fill'></div>"); //progressBarFill
		bar.setStyleName("faces-progressBar");
		return bar;
	}	
}