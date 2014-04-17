/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.invoker;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 * @deprecated
 */
@Deprecated
@Legacy
public class InvokerProxyCreator extends AbstractWrapperProxyCreator
{
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public InvokerProxyCreator(TreeLogger logger, GeneratorContext context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
    }
	
	/**
	 * 
	 * @param declaringClass
	 * @return
	 */
    public static String getControllerName(JClassType declaringClass)
	{
    	String name = null;
    	org.cruxframework.crux.core.client.controller.ControllerName annotation = declaringClass.getAnnotation(org.cruxframework.crux.core.client.controller.ControllerName.class);
		if (annotation != null)
		{
			name = annotation.value();
			if (!ClientControllers.hasController(name))
			{
				throw new CruxGeneratorException("Error generating invoker. Controller ["+name+"] not found.");
			}
		}
		else
		{
			throw new CruxGeneratorException("Error generating invoker. Controller not found for interface ["+declaringClass.getParameterizedQualifiedSourceName()+"].");
		}
		return name;
	}
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		Screen.class.getCanonicalName(),
	    		GWT.class.getCanonicalName(),
	    		Crux.class.getCanonicalName(),
	    		Window.class.getCanonicalName()
			};
		    return imports;    
    }

	/**
	 * 
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param prefixLength
	 * @throws WrapperGeneratorException 
	 */
	private void generateMethod(JMethod method, SourcePrinter sourceWriter, String sufix) throws CruxGeneratorException
	{
		JType returnType = method.getReturnType();
		String name = method.getName();

		String methodName = name.substring(0, name.length() - sufix.length());
		String controllerName = getControllerName(baseIntf);
		if (methodName.length() > 0)
		{
			String returnTypeDeclaration = returnType.getParameterizedQualifiedSourceName();
			int numParams = 0;
			sourceWriter.print("public "+returnTypeDeclaration+" " + name+"(");
			JParameter[] parameters = method.getParameters();
			for (JParameter parameter : parameters)
			{
				if (numParams > 0)
				{
					sourceWriter.print(",");
				}
				sourceWriter.print(parameter.getType().getParameterizedQualifiedSourceName()+" param"+(numParams++));
			} 
			sourceWriter.println("){");
			
			generateMethodInvocation(sourceWriter, sufix, controllerName, methodName, numParams, returnType);
			
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param sufix
	 * @param controllerName
	 * @param methodName
	 * @param numParams
	 * @param returnTypeDeclaration
	 */
	private void generateMethodInvocation(SourcePrinter sourceWriter, String sufix, String controllerName, String methodName, 
										  int numParams, JType returnType)
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
		
		String frameName = null;
		if (sufix.startsWith("OnFrame"))
		{
			frameName = sufix.substring(7);
			if (frameName.length()>1)
			{
				frameName = Character.toLowerCase(frameName.charAt(0)) + frameName.substring(1);
			}
			else
			{
				frameName = frameName.toLowerCase();
			}
			sufix = "OnFrame";
		}
		else if (sufix.startsWith("OnSiblingFrame"))
		{
			frameName = sufix.substring(14);
			if (frameName.length()>1)
			{
				frameName = Character.toLowerCase(frameName.charAt(0)) + frameName.substring(1);
			}
			else
			{
				frameName = frameName.toLowerCase();
			}
			sufix = "OnSiblingFrame";
		}
		boolean hasReturn = returnType != JPrimitiveType.VOID && !returnType.getQualifiedSourceName().equals("java.lang.Void");
		if (hasReturn)
		{
			String returnParameterizedTypeName = null;
			String returnQualifiedTypeName = null;			
			
			if (returnType.isPrimitive() != null)
			{
				returnParameterizedTypeName = returnType.isPrimitive().getQualifiedBoxedSourceName();
				returnQualifiedTypeName = returnType.isPrimitive().getQualifiedBoxedSourceName();
			}
			else
			{
				returnParameterizedTypeName = returnType.getParameterizedQualifiedSourceName();
				returnQualifiedTypeName = returnType.getQualifiedSourceName();
			}
			
			
			if ("OnFrame".equals(sufix) || "OnSiblingFrame".equals(sufix))
			{
				sourceWriter.println("return ("+returnParameterizedTypeName+")Screen.invokeController"+sufix+"(\""+frameName+"\",\""+controllerName+"."+methodName+"\","
						+(hasValue?"value":"null")+", "+returnQualifiedTypeName+".class);");
			}
			else
			{
				sourceWriter.println("return ("+returnParameterizedTypeName+")Screen.invokeController"+sufix+"(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+", "
						+returnQualifiedTypeName+".class);");
			}
		}
		else
		{
			if ("OnFrame".equals(sufix) || "OnSiblingFrame".equals(sufix))
			{
				sourceWriter.println("Screen.invokeController"+sufix+"(\""+frameName+"\",\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+");");
			}
			else
			{
				sourceWriter.println("Screen.invokeController"+sufix+"(\""+controllerName+"."+methodName+"\","+(hasValue?"value":"null")+");");
			}
		}
		sourceWriter.println("}catch(Throwable e){");
		sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote("Error for invoking method. Serialization Error.")+",e);");
		sourceWriter.println("}");
		if (hasReturn)
		{
			sourceWriter.println("return null;");
		}
	}	
	
	/**
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 * @throws CruxGeneratorException
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter) throws CruxGeneratorException
	{
		String name = method.getName();
		
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
		else if (name.indexOf("OnFrame") > 0)
		{
			String sufix = name.substring(name.indexOf("OnFrame"));
			if (sufix.length() <= 7)
			{
				throw new CruxGeneratorException("Error for generating invoker wrapper: Invalid Method signature: ["+method.getReadableDeclaration()+"].");
			}
			generateMethod(method, sourceWriter, sufix);
		}
		else if (name.indexOf("OnSiblingFrame") > 0)
		{
			String sufix = name.substring(name.indexOf("OnSiblingFrame"));
			if (sufix.length() <= 14)
			{
				throw new CruxGeneratorException("Error for generating invoker wrapper: Invalid Method signature: ["+method.getReadableDeclaration()+"].");
			}
			generateMethod(method, sourceWriter, sufix);
		}
		else
		{
			throw new CruxGeneratorException("Error for generating invoker wrapper: Invalid Method signature: ["+method.getReadableDeclaration()+"].");
		}
	}
}