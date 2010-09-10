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
package br.com.sysmap.crux.tools.quickstart.server.service;

import java.util.PropertyResourceBundle;

import br.com.sysmap.crux.tools.quickstart.client.remote.WelcomeService;

/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class WelcomeServiceImpl implements WelcomeService
{
	private static String cruxVersion = null;
	
	static
	{
		cruxVersion = PropertyResourceBundle.getBundle("version").getString("version");
	}
	
	/**
	 * @see br.com.sysmap.crux.tools.quickstart.client.remote.WelcomeService#getCruxVersion()
	 */
	public String getCruxVersion()
	{
		return cruxVersion;
	}
}
