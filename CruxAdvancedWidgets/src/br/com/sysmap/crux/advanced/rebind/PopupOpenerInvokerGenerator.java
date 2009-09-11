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
package br.com.sysmap.crux.advanced.rebind;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import br.com.sysmap.crux.advanced.client.dialog.Popup;
import br.com.sysmap.crux.core.client.controller.ControllerName;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperGenerator;
import br.com.sysmap.crux.core.rebind.WrapperGeneratorException;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class PopupOpenerInvokerGenerator extends AbstractInterfaceWrapperGenerator
{
	/**
	 * @throws WrapperGeneratorException 
	 * 
	 */
	@Override
	protected void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws WrapperGeneratorException
	{
		Class<?> returnType = method.getReturnType();
		String methodName = method.getName();
		String controllerName = getControllerName(method.getDeclaringClass());
		if (methodName.length() > 0)
		{
			String returnTypeDeclaration = getParameterDeclaration(returnType);
			sourceWriter.print("public "+returnTypeDeclaration+" " + methodName+"(");
			Type[] parameterTypes = method.getGenericParameterTypes();
			
			int numParams = 0;
			for (Type parameterType : parameterTypes)
			{
				if (numParams > 0)
				{
					sourceWriter.print(",");
				}
				
				sourceWriter.print(getParameterDeclaration(parameterType)+" param"+(numParams++));
			} 
			sourceWriter.println("){");
			
			if (returnType.getName().equals("void") || returnType.getName().equals("java.lang.Void"))
			{
				generateMethodInvocation(sourceWriter, controllerName, methodName, numParams, null);
			}
			else
			{
				if (returnType.isPrimitive())
				{
					returnTypeDeclaration = getClassNameForPrimitive(returnType);
				}
				generateMethodInvocation(sourceWriter, controllerName, methodName, numParams, returnTypeDeclaration);
			}
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param controllerName
	 * @param methodName
	 * @param numParams
	 * @param returnTypeDeclaration
	 */
	private void generateMethodInvocation(SourceWriter sourceWriter, String controllerName, String methodName, 
										  int numParams, String returnTypeDeclaration)
	{
		sourceWriter.println("try{");
		boolean hasValue = numParams > 0;
		if (numParams == 1)
		{
			sourceWriter.print("Object value = param0;");
		}
		else if (numParams > 1)
		{
			sourceWriter.print("Object[] value = new Object[]{");
			for(int i=0; i< numParams; i++)
			{
				if (i>0)
				{
					sourceWriter.print(",");
				}
				sourceWriter.print("param"+i);
			}
			sourceWriter.print("};");
		}
		
		if (returnTypeDeclaration != null)
		{
			sourceWriter.println("return " + Popup.class.getName() + ".invokeOnOpener(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+", "+returnTypeDeclaration+".class);");
		}
		else
		{
			sourceWriter.println(Popup.class.getName() + ".invokeOnOpener(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+");");
		}
		sourceWriter.println("}catch(Throwable e){");
		sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote(messages.errorInvokerWrapperSerializationError())+",e);");
		sourceWriter.println("}");
		if (returnTypeDeclaration != null)
		{
			sourceWriter.println("return null;");
		}
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