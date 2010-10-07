/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
/**
 * This package contains all the crux's client engine.
 */
package br.com.sysmap.crux.core.client;

import br.com.sysmap.crux.core.client.config.CruxClientConfig;
import br.com.sysmap.crux.core.client.errors.ErrorHandler;
import br.com.sysmap.crux.core.client.errors.ValidationErrorHandler;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;


/**
 * CRUX Client Engine. It starts a ScreenFactory to search HTML page for span tags declaring widgets. 
 * @author Thiago da Rosa de Bustamante
 */
public class Crux implements EntryPoint 
{
	private static CruxClientConfig config;
	private static ErrorHandler errorHandler;
	private static boolean initialized = false;
	private static ClientMessages messages;
	private static ValidationErrorHandler validationErrorHandler;
	
	/**
	 * 
	 * @return
	 */
	public static CruxClientConfig getConfig()
	{
		return config;
	}

	/**
	 * 
	 * @return
	 */
	public static ErrorHandler getErrorHandler()
	{
		return errorHandler;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ClientMessages getMessages()
	{
		return messages;
	}

	/**
	 * @return
	 */
	public static boolean isInitialized()
	{
		return initialized;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ValidationErrorHandler getValidationErrorHandler()
	{
		return validationErrorHandler;
	}

	/**
	 * 
	 */
	public static void initializeEngine()
	{
		try 
		{
			if (initialized)
			{
				Crux.getErrorHandler().handleError(new IllegalStateException(messages.cruxAlreadyInitializedError()));
			}
			else
			{
				messages = GWT.create(ClientMessages.class);
				config = GWT.create(CruxClientConfig.class);
				errorHandler = GWT.create(ErrorHandler.class);
				validationErrorHandler = GWT.create(ValidationErrorHandler.class);
				br.com.sysmap.crux.core.client.screen.ScreenFactory.getInstance().getScreen();
				initialized = true;
			}
		} 
		catch (Throwable e) 
		{
			if (Crux.errorHandler != null)
			{
				Crux.getErrorHandler().handleError(e);
			}
		}
		
		stopLoadingProgressBar();
	}

	/**
	 * 
	 * @param errorHandler
	 */
	public static void setErrorHandler(ErrorHandler errorHandler)
	{
		if (errorHandler == null)
		{
			Crux.errorHandler = GWT.create(ErrorHandler.class);
		}
		else
		{
			Crux.errorHandler = errorHandler;
		}
	}

	/**
	 * 
	 * @param validationErrorHandler
	 */
	public static void setValidationErrorHandler(ValidationErrorHandler validationErrorHandler)
	{
		if (validationErrorHandler == null)
		{
			Crux.validationErrorHandler = GWT.create(ValidationErrorHandler.class);
		}
		else
		{
			Crux.validationErrorHandler = validationErrorHandler;
		}
	}

	/**
	 * 
	 */
	private static void stopLoadingProgressBar()
	{
		final Element loadElement = DOM.getElementById("cruxSplashScreen");
		if (loadElement != null)
		{
			String transactionDelay = loadElement.getAttribute("_transactionDelay");
			int delay = 1;
			if (!StringUtils.isEmpty(transactionDelay))
			{
				delay = Integer.parseInt(transactionDelay);
			}
			final Element parent = loadElement.getParentElement();
			if (parent != null)
			{
				new Timer()
				{
					@Override
					public void run()
					{
						parent.removeChild(loadElement);
					}
				}.schedule(delay);
			}
		}
	}

	/**
	 * This is the entry point method. Called when the page is loaded.
	 */
	public void onModuleLoad() 
	{
		initializeEngine();
	}
}
