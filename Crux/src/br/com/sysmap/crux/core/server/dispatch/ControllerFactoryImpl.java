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
package br.com.sysmap.crux.core.server.dispatch;

import java.net.URL;

import javax.servlet.ServletContext;

import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;


public class ControllerFactoryImpl implements ControllerFactory 
{
	@Override
	public Object getController(String controllerName) 
	{
		try 
		{
			return Controllers.getController(controllerName).newInstance();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating controller "+controllerName+". Cause: "+e.getMessage(), e);
		} 
	}

	@Override
	public void initialize(ServletContext context) 
	{
		boolean lookupWebInfOnly = ("true".equals(ConfigurationFactory.getConfiguration().lookupWebInfOnly()));
		URL[] urls = ScannerURLS.getURLsForSearch(lookupWebInfOnly?context:null);
		Controllers.initialize(urls);
	}
}
