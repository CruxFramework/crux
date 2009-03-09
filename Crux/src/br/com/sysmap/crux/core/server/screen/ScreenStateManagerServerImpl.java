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
package br.com.sysmap.crux.core.server.screen;

import javax.servlet.http.HttpServletRequest;

public class ScreenStateManagerServerImpl extends AbstractScreenStateManager implements ScreenStateManager 
{
	private static final String SCREEN_KEY = "_SCREEN_KEY_";
	
	@Override
	public Screen getScreen(String screenName, HttpServletRequest request) throws ScreenConfigException {
		
		Screen result = (Screen)request.getSession().getAttribute(SCREEN_KEY);
		if (result == null)
		{
			result = getScreen(screenName);
			request.getSession().setAttribute(SCREEN_KEY, result);
		}
		
		return result;
	}

	@Override
	public boolean clientMustPreserveState() 
	{
		return false;
	}
}
