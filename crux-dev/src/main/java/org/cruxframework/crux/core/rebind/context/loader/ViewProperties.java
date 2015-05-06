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
package org.cruxframework.crux.core.rebind.context.loader;

import java.util.List;

import org.cruxframework.crux.core.declarativeui.screen.ScreenException;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewProperties
{
	private ViewProperties(){}
	
	public static final String VIEW_BASE_FOLDER = "view.base.folder";
	
	public static List<String> readPropertyValues(GeneratorContext context, String property)
	{
	    try
	    {
	        ConfigurationProperty p = context.getPropertyOracle().getConfigurationProperty(property);
	        return p.getValues();
	    }
	    catch (BadPropertyValueException e)
	    {
	    	throw new ScreenException("Error reading " + property + " property from module file.", e);
	    }
	}
	
}
