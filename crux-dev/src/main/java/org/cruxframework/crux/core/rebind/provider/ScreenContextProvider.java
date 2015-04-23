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
package org.cruxframework.crux.core.rebind.provider;

import java.io.InputStream;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.screen.ScreenException;
import org.cruxframework.crux.core.declarativeui.screen.ScreenProvider;
import org.cruxframework.crux.core.declarativeui.view.ViewProvider;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenContextProvider implements ScreenProvider
{
	private GeneratorContext context;
	private ViewContextProvider viewContextProvider;

	public ScreenContextProvider(GeneratorContext context)
    {
		this.context = context;
		viewContextProvider = new ViewContextProvider(context);
    }
	
	@Override
    public ViewProvider getViewProvider()
    {
	    return viewContextProvider;
    }

	@Override
    public InputStream getScreen(String id) throws ScreenException
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Set<String> getScreens(String module)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

}
