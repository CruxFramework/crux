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

import java.lang.reflect.Type;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractGenerator extends Generator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	/**
	 * 
	 * @param handlerClass
	 * @return
	 */
	protected static String getClassSourceName(Type type)
	{
		return ClassUtils.getTypeDeclaration(type);
	}
	
	/**
	 * 
	 * @param classType
	 * @return
	 */
	protected String getClassBinaryName(JClassType classType)
	{
		return ClassUtils.getClassBinaryName(classType);
	}
	
	/**
	 * 
	 * @param parameterType
	 * @return
	 */
	protected String getParameterDeclaration(Type parameterType)
	{
		return ClassUtils.getTypeDeclaration(parameterType);
	}
}
