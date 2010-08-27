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
package br.com.sysmap.crux.core.rebind.screen;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.rebind.AbstractWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
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
	public ScreenWrapperProxyCreator(TreeLogger logger, GeneratorContext context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
	    try
        {
	        widgetType = invokerIntf.getOracle().getType(Widget.class.getCanonicalName());
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }
	
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
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
	protected void generateWrapperMethod(JMethod method, SourceWriter sourceWriter, JClassType interfaceClass)
	{
		JType returnType = method.getReturnType();
		
		JClassType returnTypeClass = returnType.isClass();
		String name = method.getName();
		if (returnTypeClass != null && name.startsWith("get") && widgetType.isAssignableFrom(returnTypeClass) && method.getParameters().length == 0)
		{
			String widgetName = name.substring(3);
			if (widgetName.length() > 0)
			{
				String widgetNameFirstLower = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
				String classSourceName = returnType.getParameterizedQualifiedSourceName();
				sourceWriter.println("public "+classSourceName+" " + name+"(){");
				sourceWriter.println("if (Screen.contains(\""+widgetNameFirstLower+"\")){");
				sourceWriter.println("return ("+classSourceName+")Screen.get(\""+widgetNameFirstLower+"\");");
				sourceWriter.println("}else{");
				sourceWriter.println("return ("+classSourceName+")Screen.get(\""+widgetName+"\");");
				sourceWriter.println("}");
				sourceWriter.println("}");
			}
		}
	}
}
