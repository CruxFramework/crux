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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;


/**
 * CRUX Client Engine. It starts a ScreenFactory to search HTML page for span tags declaring widgets. 
 */
public class JSEngine implements EntryPoint 
{
	public static ClientMessages messages;
	public static CruxClientConfig config;
	
	/**
	 * This is the entry point method. Called when the page is loaded.
	 */
	public void onModuleLoad() 
	{
		try 
		{
			messages = GWT.create(ClientMessages.class);
			config = GWT.create(CruxClientConfig.class);
			br.com.sysmap.crux.core.client.component.ScreenFactory.getInstance().getScreen();
		} 
		catch (Throwable e) 
		{
			GWT.log(e.getLocalizedMessage(), e);
		}
		
		stopLoadingProgressBar();
	}
	
	/**
	 * 
	 */
	protected void stopLoadingProgressBar()
	{
		Element loadElement = DOM.getElementById("cruxSplashScreen");
		if (loadElement != null)
		{
			Element parent = loadElement.getParentElement();
			if (parent != null)
			{
				parent.removeChild(loadElement);
			}
		}
	}
}
