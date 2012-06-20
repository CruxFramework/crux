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
package org.cruxframework.crux.core.rebind.screen.wrapper;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenWrapperProxyCreator extends AbstractWrapperProxyCreator
{
	private final JClassType widgetType;

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public ScreenWrapperProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
	    try
        {
	        widgetType = invokerIntf.getOracle().getType(IsWidget.class.getCanonicalName());
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }
		
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyMethods(srcWriter);
	    generateScreenGetterMethod(srcWriter);
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
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourceWriter sourceWriter)
	{
		JType returnType = method.getReturnType();
		
		JClassType returnTypeClass = returnType.isClassOrInterface();
		String name = method.getName();
		if (widgetType.isAssignableFrom(returnTypeClass))
		{
			if(method.getParameters().length == 0)
			{
				String widgetName;
				if (returnTypeClass != null && name.startsWith("get"))
				{
					widgetName = name.substring(3);
					if (widgetName.length() > 0)
					{
						generateWrapperMethodForGetter(sourceWriter, returnType, name, widgetName);
					}
				}
				else
				{
					widgetName = name;
					generateWrapperMethod(sourceWriter, returnType, name, widgetName);
				}
			}
			else
			{
				throw new CruxGeneratorException("The method ["+method.getName()+"] from ScreenWrapper ["+method.getEnclosingType().getQualifiedSourceName()+"] must have no parameters.");
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+method.getName()+"] from ScreenWrapper ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a subclass of com.google.gwt.user.client.ui.Widget.");
		}
	}

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param widgetName
	 */
	private void generateWrapperMethod(SourceWriter sourceWriter, JType returnType, String name, String widgetName)
    {
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + name+"(){");
		sourceWriter.indent();
		sourceWriter.println("return ("+classSourceName+")Screen.get(\""+widgetName+"\");");
		sourceWriter.outdent();
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param widgetName
	 */
	private void generateWrapperMethodForGetter(SourceWriter sourceWriter, JType returnType,
			String name, String widgetName)
	{
		String widgetNameFirstLower = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + name+"(){");
		sourceWriter.indent();
		sourceWriter.println("return ("+classSourceName+")_getFromScreen(\""+widgetNameFirstLower+"\");");
		sourceWriter.outdent();

		sourceWriter.println("}");
	}

	private void generateScreenGetterMethod(SourceWriter srcWriter)
	{
		srcWriter.println("public Object _getFromScreen(String widgetName){");
		srcWriter.indent();
		srcWriter.println("Object ret = Screen.get(widgetName);");
		srcWriter.println("if (ret == null){");
		srcWriter.indent();
		srcWriter.println("String widgetNameFirstUpper;");
		srcWriter.println("if (widgetName.length() > 1){"); 
		srcWriter.indent();
		srcWriter.println("widgetNameFirstUpper = Character.toUpperCase(widgetName.charAt(0)) + widgetName.substring(1);");
		srcWriter.outdent();
		srcWriter.println("}");
		srcWriter.println("else{"); 
		srcWriter.indent();
		srcWriter.println("widgetNameFirstUpper = \"\"+Character.toUpperCase(widgetName.charAt(0));");
		srcWriter.outdent();
		srcWriter.println("}");
		srcWriter.println("ret = Screen.get(widgetNameFirstUpper);");
		srcWriter.outdent();
		srcWriter.println("}");
		srcWriter.println("return ret;");
		srcWriter.outdent();
		srcWriter.println("}");
	}
}
