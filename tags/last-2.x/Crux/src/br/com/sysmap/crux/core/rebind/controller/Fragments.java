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
package br.com.sysmap.crux.core.rebind.controller;

import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.utils.ClassUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Fragments
{
	public static Map<String, String> fragmentClasses = new HashMap<String, String>();
	
	/**
	 * 
	 * @param fragment
	 * @param clazz
	 */
	public static void registerFragment(String fragment, Class<?> clazz)
	{
		if (!StringUtils.isEmpty(fragment) && !fragmentClasses.containsKey(fragment))
		{
			fragmentClasses.put(fragment, ClassUtils.getClassSourceName(clazz));
		}
	}
	
	/**
	 * 
	 * @param fragment
	 * @return
	 */
	public static String getFragmentClass(String fragment)
	{
		return fragmentClasses.get(fragment);
	}
}
