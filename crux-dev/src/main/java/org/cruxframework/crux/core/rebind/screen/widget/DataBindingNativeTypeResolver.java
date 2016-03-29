/*
 * Copyright 2016 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Thiago da Rosa de Bustamante
 */
class DataBindingNativeTypeResolver
{
	private static Set<String> booleanProperties;
	private static Set<String> intProperties;
	private static Set<String> doubleProperties;
	
	public static enum PropertyType
	{
		booleanProperty("boolean", "getPropertyBoolean", "setPropertyBoolean"),
		intProperty("int", "getPropertyInt", "setPropertyInt"),
		doubleProperty("double", "getPropertyDouble", "setPropertyDouble"),
		stringProperty("java.lang.String", "getPropertyString", "setPropertyString");
		
		private String type;
		private String getter;
		private String setter;

		PropertyType(String type, String getter, String setter)
		{
			this.type = type;
			this.getter = getter;
			this.setter = setter;
		}

		public String getType()
		{
			return type;
		}

		public String getGetter()
		{
			return getter;
		}

		public String getSetter()
		{
			return setter;
		}
	}
	
	private static Set<String> getBooleanProperties()
	{
		if (booleanProperties == null)
		{
			booleanProperties = new HashSet<String>();
			String[] props = {"disabled", "checked", "defaultchecked", "readonly", "multiple", "required", "selected", "nowrap"};
			
			booleanProperties.addAll(Arrays.asList(props));
		}
		return booleanProperties;
	}
	
	private static Set<String> getDoubleProperties()
	{
		if (doubleProperties == null)
		{
			doubleProperties = new HashSet<String>();
			String[] props = {"position"};
			
			doubleProperties.addAll(Arrays.asList(props));
		}
		return doubleProperties;
	}
	
	private static Set<String> getIntProperties()
	{
		if (intProperties == null)
		{
			intProperties = new HashSet<String>();
			String[] props = {"colspan", "rowspan", "cellpadding", "cellspacing", 
				"step", "size", "max", "min", "maxlength", "low", "high", "optimum"};
			
			intProperties.addAll(Arrays.asList(props));
		}
		return intProperties;
	}

	public static PropertyType resolveTypeForProperty(String property)
	{
		String propertyLower = property.toLowerCase();
		if (getBooleanProperties().contains(propertyLower))
		{
			return PropertyType.booleanProperty;
		}
		if (getIntProperties().contains(propertyLower))
		{
			return PropertyType.intProperty;
		}
		if (getDoubleProperties().contains(propertyLower))
		{
			return PropertyType.doubleProperty;
		}
		return PropertyType.stringProperty;
	}
}
