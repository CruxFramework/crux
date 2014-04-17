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
package org.cruxframework.crux.widgets.rebind;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.invoker.InvokerProxyCreator;
import org.cruxframework.crux.widgets.client.dialog.Popup;

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
 * Generator for PopupOpenerInvoker objects.
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class PopupOpenerInvokerProxyCreator extends AbstractWrapperProxyCreator
{
	/**
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public PopupOpenerInvokerProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
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
	 * @throws WrapperGeneratorException 
	 * 
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter) throws CruxGeneratorException
	{
		JType returnType = method.getReturnType();
		String methodName = method.getName();
		String controllerName = InvokerProxyCreator.getControllerName(baseIntf);
		if (methodName.length() > 0)
		{
			String returnTypeDeclaration = returnType.getParameterizedQualifiedSourceName();
			sourceWriter.print("public "+returnTypeDeclaration+" " + methodName+"(");
			JParameter[] parameters = method.getParameters();
			
			int numParams = 0;
			for (JParameter parameter : parameters)
			{
				if (numParams > 0)
				{
					sourceWriter.print(",");
				}
				
				sourceWriter.print(parameter.getType().getParameterizedQualifiedSourceName()+" param"+(numParams++));
			} 
			sourceWriter.println("){");
			
			if (returnType == JPrimitiveType.VOID || returnType.getQualifiedSourceName().equals("java.lang.Void"))
			{
				generateMethodInvocation(sourceWriter, controllerName, methodName, numParams, null);
			}
			else
			{
				if (returnType.isPrimitive() != null)
				{
					returnTypeDeclaration = returnType.isPrimitive().getQualifiedBoxedSourceName();
				}
				else
				{
					returnTypeDeclaration = returnType.getQualifiedSourceName();
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
	private void generateMethodInvocation(SourcePrinter sourceWriter, String controllerName, String methodName, 
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
		sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote("Error invoking method. Serialization Error.")+",e);");
		sourceWriter.println("}");
		if (returnTypeDeclaration != null)
		{
			sourceWriter.println("return null;");
		}
	}
}