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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import br.com.sysmap.crux.core.client.context.ContextManager;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ContextGenerator extends AbstractInterfaceWrapperGenerator
{
	/**
	 * @throws WrapperGeneratorException 
	 * 
	 */
	@Override
	protected void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws WrapperGeneratorException
	{
		Class<?> returnType = method.getReturnType();
		String name = method.getName();
		if (name.startsWith("get") && method.getParameterTypes().length == 0 && 
		   (!method.getReturnType().getName().equals("void") && !method.getReturnType().getName().equals("java.lang.Void")))
		{
			generateGetter(method, sourceWriter, returnType, name, 3);
		}
		else if (name.startsWith("is") && method.getParameterTypes().length == 0 && 
				(!method.getReturnType().getName().equals("void") && !method.getReturnType().getName().equals("java.lang.Void")))
		{
			generateGetter(method, sourceWriter, returnType, name, 2);
		}
		else if (name.startsWith("set") && method.getParameterTypes().length == 1 && 
				(method.getReturnType().getName().equals("void") || method.getReturnType().getName().equals("java.lang.Void")))
		{
			generateSetter(method, sourceWriter, name);
		}
		else
		{
			throw new WrapperGeneratorException(messages.errorContextWrapperInvalidSignature(method.toGenericString()));
		}
	}

	/**
	 * 
	 * @param method
	 * @param sourceWriter
	 * @param name
	 * @throws WrapperGeneratorException 
	 */
	private void generateSetter(Method method, SourceWriter sourceWriter, String name) throws WrapperGeneratorException
	{
		Type parameterType = method.getGenericParameterTypes()[0];
		if ((parameterType instanceof Class<?>) && ((Class<?>)parameterType).isPrimitive())
		{
			throw new WrapperGeneratorException(messages.errorContextWrapperPrimitiveParamterNotAllowed(method.toGenericString()));
		}
		String propertyName = name.substring(3);
		if (propertyName.length() > 0)
		{
			propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
			sourceWriter.println("public void " + name+"("+getParameterDeclaration(parameterType)+" value){");
			sourceWriter.println("if (value == null){");
			sourceWriter.println(ContextManager.class.getName()+".getContextHandler().eraseData(\""+propertyName+"\");");
			sourceWriter.println("} else {");
			sourceWriter.println(ContextManager.class.getName()+".getContextHandler().writeData(\""+propertyName+"\", value);");
			sourceWriter.println("}");
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param prefixLength
	 * @throws WrapperGeneratorException 
	 */
	private void generateGetter(Method method, SourceWriter sourceWriter, Class<?> returnType, String name, int prefixLength) throws WrapperGeneratorException
	{
		if (returnType.isPrimitive())
		{
			throw new WrapperGeneratorException(messages.errorContextWrapperPrimitiveParamterNotAllowed(method.toGenericString()));
		}

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
}
