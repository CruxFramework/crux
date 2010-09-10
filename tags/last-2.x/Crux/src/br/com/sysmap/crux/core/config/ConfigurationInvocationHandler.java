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
package br.com.sysmap.crux.core.config;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import br.com.sysmap.crux.core.client.utils.StringUtils;

/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante
 * @author Gess� S. F. Daf�
 */
public class ConfigurationInvocationHandler extends ConstantsInvocationHandler
{
	private String interfaceSimpleName;
	
	public ConfigurationInvocationHandler(Class<?> targetInterface) 
	{
		super(targetInterface);
		this.interfaceSimpleName = targetInterface.getSimpleName();
	}
	
	@Override
	protected <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface) 
	{
		return loadProperties(targetInterface, Locale.getDefault());
	}
	
	@Override
	protected String getMessageFromProperties(Object[] args, String name)
	{
		String property = System.getProperty(interfaceSimpleName+"."+name);
		
		if (!StringUtils.isEmpty(property))
		{
			return property;
		}
		
		return super.getMessageFromProperties(args, name);
	}
}
