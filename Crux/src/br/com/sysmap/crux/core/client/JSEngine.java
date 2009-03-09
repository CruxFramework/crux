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

import br.com.sysmap.crux.core.client.config.CruxConfig;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * CRUX Client Engine. Use <code>span</code> tags as templates for components 
 * to build the interface. 
 */
public class JSEngine implements EntryPoint 
{
	public static ClientMessages messages;
	public static CruxConfig cruxConfig;
	
	/**
	 * This is the entry point method. Called when the page is loaded.
	 */
	public void onModuleLoad() 
	{
		try 
		{
			messages = (ClientMessages) GWT.create(ClientMessages.class);
			cruxConfig = (CruxConfig) GWT.create(CruxConfig.class);
			br.com.sysmap.crux.core.client.component.ScreenFactory.getInstance().getScreen();
		} 
		catch (Throwable e) 
		{
			GWT.log(e.getLocalizedMessage(), e);
		}
	}
}
// TODO: Explicitar na documentação todos os pontos de extensão.