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
package org.cruxframework.crux.widgets.client.dialog;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple widget without layout for showing typical "Please, wait..." message boxes across frames. 
 * The rendering is made by <code>CruxInternalProgressDialogController</code>, using the attributes contained in this widget.
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class ProgressDialog extends Widget implements HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "crux-ProgressDialog" ;
	private static CruxInternalProgressDialogController progressDialogController = null;
	private String message;
	private String styleName;
	private boolean animationEnabled;
	protected static ProgressDialog progressDialog;
	
	/**
	 * Default constructor
	 */
	public ProgressDialog()
	{
		setElement(DOM.createSpan());
	}

	/**
	 * Calls the <code>CruxInternalProgressDialogController</code> for showing the progress dialog
	 */
	public void show()
	{
		if (progressDialogController == null)
		{
			progressDialogController = new CruxInternalProgressDialogController(); 
		}
		progressDialog = this;
		progressDialogController.showProgressDialog(new ProgressDialogData(message, styleName != null ? styleName : DEFAULT_STYLE_NAME, animationEnabled));
	}
	
	/**
	 * Hides the progress dialog 
	 */
	public static void hide()
	{
		if (progressDialogController == null)
		{
			progressDialogController = new CruxInternalProgressDialogController(); 
		}
		progressDialogController.hideProgressDialog();
	}
	
	/**
	 * Shows a cross frame progress dialog
	 * @param message the text to be displayed in the body of the message box 
	 */
	public static void show(String message)
	{
		show(message, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * Shows a cross frame progress dialog
	 * @param message the text to be displayed in the body of the message box
	 * @param styleName the name of the CSS class to be applied in the message box element
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public static void show(String message, String styleName, boolean animationEnabled)
	{
		ProgressDialog progressDialog = new ProgressDialog(); 
		progressDialog.setMessage(message);
		progressDialog.setStyleName(styleName);
		progressDialog.setAnimationEnabled(animationEnabled);
		progressDialog.show();
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the styleName
	 */
	public String getStyleName()
	{
		return styleName;
	}

	/**
	 * @param styleName the styleName to set
	 */
	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	/**
	 * @return the animationEnabled
	 */
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * @param animationEnabled the animationEnabled to set
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}
}