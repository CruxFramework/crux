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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import br.com.sysmap.crux.core.client.context.ContextManager;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ContextGenerator extends AbstractInterfaceWrapperGenerator
{
	/**
	 * 
	 */
	protected void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter)
	{
		Class<?> returnType = method.getReturnType();
		String name = method.getName();
		if (name.startsWith("get") && method.getParameterTypes().length == 0)
		{
			generateGetter(sourceWriter, returnType, name, 3);
		}
		else if (name.startsWith("is") && method.getParameterTypes().length == 0)
		{
			generateGetter(sourceWriter, returnType, name, 2);
		}
		else if (name.startsWith("set") && method.getParameterTypes().length == 1)
		{
			generateSetter(method, sourceWriter, name);
		}
	}

	/**
	 * 
	 * @param method
	 * @param sourceWriter
	 * @param name
	 */
	private void generateSetter(Method method, SourceWriter sourceWriter, String name)
	{
		Type parameterType = method.getGenericParameterTypes()[0];
		String propertyName = name.substring(3);
		if (propertyName.length() > 0)
		{
			propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
			sourceWriter.println("public void " + name+"("+getParameterDeclaration(parameterType)+" value){");
			sourceWriter.println(ContextManager.class.getName()
								 +".getContextHandler().writeData(\""+propertyName+"\", value);");
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param prefixLength
	 */
	private void generateGetter(SourceWriter sourceWriter, Class<?> returnType, String name, int prefixLength)
	{
		String propertyName = name.substring(prefixLength);
		if (propertyName.length() > 0)
		{
			propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
			String returnTypeDeclaration = getParameterDeclaration(returnType);
			sourceWriter.println("public "+returnTypeDeclaration+" " + name+"(){");
			sourceWriter.println("return ("+ returnTypeDeclaration+") "+ContextManager.class.getName()
								 +".getContextHandler().readData(\""+propertyName+"\");");
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param parameterType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getParameterDeclaration(Type parameterType)
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
				result.append(parameterClass.getName());
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
		String name = parameterClass.getName();
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
