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
package br.com.sysmap.crux.core.rebind;


/**
 * A Bridge class for allow Generators to know the name of the module 
 * that starts the generation process. Crux Generators need this information
 * to obtain better performance for method handlers in client side of 
 * applications.
 * 
 * @author Thiago
 *
 */
public class CruxScreenBridge 
{
	private static CruxScreenBridge instance = new CruxScreenBridge();
	public String lastPage = null;
	private CruxScreenBridge() 
	{
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static CruxScreenBridge getInstance()
	{
		return instance;
	}
	
	/** 
	 * Inform the name of the last page the client requested. This is used
	 * only in hosted mode of GWT, when we will have only the developer
	 * working on a page.
	 */
	public void registerLastPageRequested(String lastPage)
	{
		this.lastPage = lastPage;
	}
	
	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getLastPageRequested() 
	{
		return this.lastPage;
	}
	
}
