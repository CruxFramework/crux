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
import org.cruxframework.crux.core.client.screen.views.Target;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.rebind.AbstractViewBindableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetAccessorProxyCreator extends AbstractViewBindableProxyCreator
{
	private final JClassType widgetType;

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public WidgetAccessorProxyCreator(TreeLogger logger, GeneratorContext context, JClassType invokerIntf)
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
    protected String[] getImports()
    {
		String[] imports = new String[] {
				IsWidget.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyMethods(srcWriter);
		generateViewGetterMethod(srcWriter);
	}
	
	/**
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter)
	{
		JType returnType = method.getReturnType();
		
		JClassType returnTypeClass = returnType.isClassOrInterface();
		String name = method.getName();
		if (widgetType.isAssignableFrom(returnTypeClass))
		{
			if(method.getParameters().length == 0)
			{
				String widgetName;
				Target target = method.getAnnotation(Target.class);
				if (target != null) 
				{
					widgetName = target.value();
					generateWrapperMethod(sourceWriter, returnType, name, widgetName);
				}
				else if (returnTypeClass != null && name.startsWith("get"))
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
				throw new CruxGeneratorException("The method ["+method.getName()+"] from WidgetAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must have no parameters.");
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+method.getName()+"] from WidgetAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a subclass of com.google.gwt.user.client.ui.Widget.");
		}
	}

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param widgetName
	 */
	private void generateWrapperMethod(SourcePrinter sourceWriter, JType returnType, String name, String widgetName)
    {
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + name+"(){");
		sourceWriter.println(View.class.getCanonicalName()+" __view = "+View.class.getCanonicalName()+".getView(this.__view);");
		generateCheckView(sourceWriter);
		sourceWriter.println("return ("+classSourceName+")__view.getWidget(\""+widgetName+"\");");
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param widgetName
	 */
	private void generateWrapperMethodForGetter(SourcePrinter sourceWriter, JType returnType,
			String name, String widgetName)
	{
		String widgetNameFirstLower = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + name+"(){");
		sourceWriter.println("return ("+classSourceName+")_getFromView(\""+widgetNameFirstLower+"\");");
		sourceWriter.println("}");
	}
}
