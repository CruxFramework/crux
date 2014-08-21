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
import org.cruxframework.crux.smartfaces.client.progress.Progress;

import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * A simple dialog which shows a progressbar and a message
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class ProgressBox extends AbstractDialogBox implements HasValue<Integer>
{
	public static final String DEFAULT_STYLE_NAME = "faces-ProgressBox";
	private Progress progress;

	/**
	 * Creates a wait box
	 */
	public ProgressBox()
	{
		this(true);
	}
	
	/**
	 * Creates a wait box
	 * @param movable
	 */
	public ProgressBox(boolean movable)
	{
		super(movable, false, false, true, DEFAULT_STYLE_NAME);
		progress = Progress.createIfSupported();
		setWidget(progress);
	}

	/**
	 * Sets the message to be shown
	 * @param message
	 */
	public void setMessage(String message)
	{
		super.setDialogTitle(message);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) 
	{
		return progress.addValueChangeHandler(handler);
	}

	@Override
	public Integer getValue() 
	{
		return progress.getValue();
	}

	@Override
	public void setValue(Integer value) 
	{
		progress.setValue(value);
	}
	
	public void setMax(int max)
	{
		progress.setMax(max);
	}
	
	public int getMax()
	{
		return progress.getMax();
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) 
	{
		progress.setValue(value, fireEvents);
	}
	
	/**
	 * Shows a progress box
	 * @param message the text to be displayed
	 */
	public static ProgressBox show(String message)
	{
		return show(message, null);
	}
	
	/**
	 * Shows a progress box
	 * @param message the text to be displayed
	 * @param animation animates the dialog while showing or hiding
	 */
	public static ProgressBox show(String message, DialogAnimation animation)
	{
		ProgressBox progressBox = ProgressBox.createIfSupported();
		if (progressBox == null)
		{
			return null;
		}
		if (animation != null)
		{
			progressBox.setAnimation(animation);
		}
		progressBox.setMessage(message);
		progressBox.center();
		return progressBox;
	}
	
	public static boolean isSupported()
	{
		return Progress.isSupported();
	}
	
	public static ProgressBox createIfSupported()
	{
		if (isSupported())
		{
			return new ProgressBox();
		}
		return null;
	}

	public static ProgressBox createIfSupported(boolean movable)
	{
		if (isSupported())
		{
			return new ProgressBox(movable);
		}
		return null;
	}
}
