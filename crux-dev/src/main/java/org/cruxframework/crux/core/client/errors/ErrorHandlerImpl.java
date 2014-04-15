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
package org.cruxframework.crux.core.client.errors;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ErrorHandlerImpl implements ErrorHandler, ValidationErrorHandler
{
	private static Logger logger = Logger.getLogger(Crux.class.getName());

	/**
	 * 
	 */
	public void handleError(String errorMessage)
	{
		handleError(errorMessage, null);
	}

	/**
	 * 
	 */
	public void handleError(Throwable t)
	{
	    handleError(t, false);
	}

	/**
	 * 
	 */
	public void handleError(String errorMessage, Throwable t)
	{
		if (t != null)
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.SEVERE, errorMessage==null?"":errorMessage, t);
			}
			GWT.log(errorMessage, t);
		}
		if (errorMessage != null)
		{
			Window.alert(errorMessage);
		}
	}

	/**
	 * 
	 */
	public void handleValidationError(String errorMessage)
	{
		Window.alert(errorMessage);
	}

	@Override
    public void handleError(Throwable t, boolean uncaught)
    {
		handleError(t.getMessage(), t);
    }

	@Override
	//TODO: when calling this handler, threat required widgets when they implement HasData.
	public Widget handleValidationError(Widget widget, String errorMessage) 
	{
		widget.addStyleName("error");
		DialogBox errorMsgDialog = new DialogBox();
		errorMsgDialog.setStyleName("errorMsg");
		errorMsgDialog.showRelativeTo(widget);
		return errorMsgDialog;
	}
}
