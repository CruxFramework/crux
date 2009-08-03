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

import br.com.sysmap.crux.core.i18n.MessagesFactory;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractGenerator extends Generator
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	/**
	 * 
	 * @param handlerClass
	 * @return
	 */
	protected String getClassSourceName(Class<?> handlerClass)
	{
		String sourceName = handlerClass.getName();
		sourceName = sourceName.replace('$','.');
		return sourceName;
	}
	
	/**
	 * 
	 * @param classType
	 * @return
	 */
	protected String getClassBinaryName(JClassType classType)
	{
		String pkgName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName();
		String name = classType.getName();
		
		if (name.equals(simpleName))
		{
			return pkgName + "." +name;
		}
		else
		{
			return pkgName + "." + name.substring(0, name.indexOf(simpleName)-1) + "$"+ simpleName;
		}
	}
}
