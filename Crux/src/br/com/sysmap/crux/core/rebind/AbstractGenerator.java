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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

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
	
	/**
	 * 
	 * @param parameterType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getParameterDeclaration(Type parameterType)
	{
		StringBuilder result = new StringBuilder();
		if (parameterType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType =((ParameterizedType)parameterType);
			result.append(getParameterDeclaration(parameterizedType.getRawType()));
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments != null && actualTypeArguments.length > 0)
			{
				result.append("<");
				for (Type type : actualTypeArguments)
				{
					result.append(getParameterDeclaration(type));
				}
				result.append(">");
			}
			
		}
		else if (parameterType instanceof GenericArrayType)
		{
			GenericArrayType genericArrayType = (GenericArrayType) parameterType;
			result.append(getParameterDeclaration(genericArrayType.getGenericComponentType()));
			result.append("[]");
		}
		else if (parameterType instanceof TypeVariable)
		{
			TypeVariable<GenericDeclaration> typeVariable = (TypeVariable<GenericDeclaration>) parameterType;
			result.append(typeVariable.getName());
			GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
			if (genericDeclaration != null)
			{
				TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
				if (typeParameters != null && typeParameters.length > 0)
				{
					result.append("<");
					for (Type type : typeParameters)
					{
						result.append(getParameterDeclaration(type));
					}
					result.append(">");
				}
				
			}
		}
		else if (parameterType instanceof Class)
		{
			Class<?> parameterClass = ((Class<?>)parameterType);
			if (parameterClass.isArray())
			{
				Class<?> componentType = parameterClass.getComponentType();
				result.append(getParameterDeclaration(componentType));
				int numDim = getArrayDimensions(parameterClass);
				for (int i=0; i<numDim; i++)
				{
					result.append("[]");
				}
			}
			else
			{
				result.append(getClassSourceName(parameterClass));
			}
		}
		else if (parameterType instanceof WildcardType)
		{
			result.append("?");
		}
		return result.toString();
	}

	/**
	 * 
	 * @param parameterClass
	 * @return
	 */
	private int getArrayDimensions(Class<?> parameterClass)
	{
		String name = getClassSourceName(parameterClass);
		for (int i=0; i<name.length(); i++)
		{
			if (name.charAt(i) != '[')
			{
				return i;
			}
		}
		return 0;
	}
}
