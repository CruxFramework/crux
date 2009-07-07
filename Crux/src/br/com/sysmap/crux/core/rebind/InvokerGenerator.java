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

import br.com.sysmap.crux.core.client.controller.ControllerName;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class InvokerGenerator extends AbstractInterfaceWrapperGenerator
{
	/**
	 * @throws WrapperGeneratorException 
	 * 
	 */
	@Override
	protected void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws WrapperGeneratorException
	{
		String name = method.getName();
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0 || parameterTypes.length == 1 )
		{
			if (name.endsWith("OnTop"))
			{
				generateMethod(method, sourceWriter, "OnTop");
			}
			else if (name.endsWith("OnParent"))
			{
				generateMethod(method, sourceWriter, "OnParent");
			}
			else if (name.endsWith("OnOpener"))
			{
				generateMethod(method, sourceWriter, "OnOpener");
			}
			else if (name.endsWith("OnAbsoluteTop"))
			{
				generateMethod(method, sourceWriter, "OnAbsoluteTop");
			}
			else if (name.endsWith("OnSelf"))
			{
				generateMethod(method, sourceWriter, "OnSelf");
			}
			else
			{
				throw new WrapperGeneratorException(messages.errorInvokerWrapperInvalidSignature(method.toGenericString()));
			}
		}
		else
		{
			throw new WrapperGeneratorException(messages.errorInvokerWrapperInvalidSignature(method.toGenericString()));
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
	private void generateMethod(Method method, SourceWriter sourceWriter, String sufix) throws WrapperGeneratorException
	{
		Class<?> returnType = method.getReturnType();
		String name = method.getName();

		String methodName = name.substring(0, name.length() - sufix.length());
		String controllerName = getControllerName(method.getDeclaringClass());
		if (methodName.length() > 0)
		{
			String returnTypeDeclaration = getParameterDeclaration(returnType);
			boolean hasValue = false;
			if (method.getParameterTypes().length > 0)
			{
				Type parameterType = method.getGenericParameterTypes()[0];
				sourceWriter.println("public "+returnTypeDeclaration+" " + name+"("+getParameterDeclaration(parameterType)+" value){");
				hasValue = true;
			}
			else
			{
				sourceWriter.println("public "+returnTypeDeclaration+" " + name+"(){");
			}
			
			if (returnType.getName().equals("void") || returnType.getName().equals("java.lang.Void"))
			{
				generateVoidMethodInvocation(sourceWriter, sufix, controllerName, methodName, hasValue);
			}
			else
			{
				if (returnType.isPrimitive())
				{
					returnTypeDeclaration = getClassNameForPrimitive(returnType);
				}
				generateMethodInvocation(sourceWriter, sufix, controllerName, methodName, hasValue, returnTypeDeclaration);
			}
			sourceWriter.println("}");
		}
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param sufix
	 * @param controllerName
	 * @param methodName
	 * @param hasValue
	 */
	private void generateVoidMethodInvocation(SourceWriter sourceWriter, String sufix, String controllerName, String methodName, boolean hasValue)
	{
		sourceWriter.println("try{");
		sourceWriter.println("Screen.invokeController"+sufix+"(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+");");
		sourceWriter.println("}catch(Throwable e){");
		sourceWriter.println("GWT.log(e.getLocalizedMessage(), e);");
		sourceWriter.println("Window.alert("+EscapeUtils.quote(messages.errorInvokerWrapperSerializationError())+");");
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param sufix
	 * @param controllerName
	 * @param methodName
	 * @param hasValue
	 * @param returnTypeDeclaration
	 */
	private void generateMethodInvocation(SourceWriter sourceWriter, String sufix, String controllerName, String methodName, 
										  boolean hasValue, String returnTypeDeclaration)
	{
		sourceWriter.println("try{");
		sourceWriter.println("return Screen.invokeController"+sufix+"(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+", "+returnTypeDeclaration+".class);");
		sourceWriter.println("}catch(Throwable e){");
		sourceWriter.println("GWT.log(e.getLocalizedMessage(), e);");
		sourceWriter.println("Window.alert("+EscapeUtils.quote(messages.errorInvokerWrapperSerializationError())+");");
		sourceWriter.println("}");
		sourceWriter.println("return null;");
	}

	/**
	 * 
	 * @param declaringClass
	 * @return
	 */
	private String getControllerName(Class<?> declaringClass)
	{
		ControllerName annotation = declaringClass.getAnnotation(ControllerName.class);
		if (annotation != null)
		{
			return annotation.value();
		}
		String name = declaringClass.getSimpleName();
		if (name.endsWith("Invoker"))
		{
			name = name.substring(0, name.lastIndexOf("Invoker"));
			if (name.length() > 0)
			{
				name =  Character.toLowerCase(name.charAt(0)) + name.substring(1);
			}
		}
		return name;
	}

	/**
	 * 
	 * @param parameterClass
	 * @return
	 */
	private String getClassNameForPrimitive(Class<?> parameterClass)
	{
		if ("int".equals(parameterClass.getName()))
		{
			return Integer.class.getName();
		}
		else if ("short".equals(parameterClass.getName()))
		{
			return Short.class.getName();
		}
		else if ("long".equals(parameterClass.getName()))
		{
			return Long.class.getName();
		}
		else if ("byte".equals(parameterClass.getName()))
		{
			return Byte.class.getName();
		}
		else if ("float".equals(parameterClass.getName()))
		{
			return Float.class.getName();
		}
		else if ("double".equals(parameterClass.getName()))
		{
			return Double.class.getName();
		}
		else if ("char".equals(parameterClass.getName()))
		{
			return Character.class.getName();
		}
		else if ("boolean".equals(parameterClass.getName()))
		{
			return Boolean.class.getName();
		}
		return null;
	}	
}
