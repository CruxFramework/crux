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
package org.cruxframework.crux.core.rebind.screen.binder;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenBinder;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenBinderProxyCreator extends AbstractWrapperProxyCreator
{
	private final JClassType screenBinderType;
	private final JClassType stringType;

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public ScreenBinderProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
	    try
        {
	    	screenBinderType = invokerIntf.getOracle().getType(ScreenBinder.class.getCanonicalName());
	    	stringType = invokerIntf.getOracle().getType(String.class.getCanonicalName());
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	    
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    	JMethod[] methods = baseIntf.getOverridableMethods();
    	for (JMethod method : methods)
    	{
    		String methodName = method.getName();
    		if (method.getName().startsWith("get"))
    		{
    			JClassType returnTypeClass = method.getReturnType().isClassOrInterface();
    			if (returnTypeClass != null && returnTypeClass.isAssignableTo(screenBinderType))
    			{
    				String sourceName = returnTypeClass.getParameterizedQualifiedSourceName();
					srcWriter.println("private "+sourceName+" _"+methodName+" = GWT.create("+sourceName+".class);");
    			}
    		}
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
				IsWidget.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				HasValue.class.getCanonicalName(),
				HasText.class.getCanonicalName(),
				HasFormatter.class.getCanonicalName()
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
		checkMethodSignature(method);

		String name = method.getName();

		String widgetName;
		if (name.startsWith("get"))
		{
			widgetName = name.substring(3);
			if (widgetName.length() > 0)
			{
				JType returnType = method.getReturnType();
				generateWrapperMethodForGetter(sourceWriter, returnType, name, widgetName);
			}
		}
		else if (name.startsWith("set"))
		{
			widgetName = name.substring(3);
			if (widgetName.length() > 0)
			{
				JType parameterType = method.getParameterTypes()[0];
				generateWrapperMethodForSetter(sourceWriter, parameterType, name, widgetName);
			}
		}
	}

	/**
	 * @param sourceWriter
	 * @param parameterType
	 * @param methodName
	 * @param widgetName
	 */
	private void generateWrapperMethodForSetter(SourceWriter sourceWriter, JType parameterType, String methodName, String widgetName)
    {
		String widgetNameFirstLower = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
		String parameterClassName = JClassUtils.getGenericDeclForType(parameterType);
		sourceWriter.println("public void " + methodName+"("+parameterType.getParameterizedQualifiedSourceName()+" value){");
		sourceWriter.indent();

		if (JClassUtils.isSimpleType(parameterType)) 
		{
			sourceWriter.println("IsWidget w = _getFromScreen(\""+widgetNameFirstLower+"\");");
			
			sourceWriter.println("if (w != null) {");
			sourceWriter.indent();
			sourceWriter.println("if (w instanceof HasFormatter) {");
			sourceWriter.indent();
			if (parameterType.isPrimitive() != null)
			{
				sourceWriter.println("((HasFormatter)w).setUnformattedValue(("+parameterClassName+")value);");
			}
			else
			{
				sourceWriter.println("((HasFormatter)w).setUnformattedValue(value);");
			}
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("else if (w instanceof HasValue) {");
			sourceWriter.indent();
			sourceWriter.println("((HasValue<"+parameterClassName+">)w).setValue(value);");
			sourceWriter.outdent();
			sourceWriter.println("}");
			if (parameterType.equals(stringType))
			{
				sourceWriter.println("else if (w instanceof HasText) {");
				sourceWriter.indent();
				sourceWriter.println("((HasText)w).setText(value);");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println("this._"+methodName+" = value;");
		}
		
		sourceWriter.outdent();
		sourceWriter.println("}");
    }	

	/**
	 * 
	 * @param sourceWriter
	 * @param returnType
	 * @param methodName
	 * @param widgetName
	 */
	private void generateWrapperMethodForGetter(SourceWriter sourceWriter, JType returnType, String methodName, String widgetName)
	{
		String widgetNameFirstLower = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
		String returnClassName = JClassUtils.getGenericDeclForType(returnType);
		sourceWriter.println("public "+returnType.getParameterizedQualifiedSourceName()+" " + methodName+"(){");
		sourceWriter.indent();

		if (JClassUtils.isSimpleType(returnType)) 
		{
			sourceWriter.println("IsWidget w = _getFromScreen(\""+widgetNameFirstLower+"\");");
			
			sourceWriter.println("if (w != null) {");
			sourceWriter.indent();
			sourceWriter.println("if (w instanceof HasFormatter) {");
			sourceWriter.indent();
			sourceWriter.println("return ("+returnClassName+")((HasFormatter)w).getUnformattedValue();");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("else if (w instanceof HasValue) {");
			sourceWriter.indent();
			sourceWriter.println("return ((HasValue<"+returnClassName+">)w).getValue();");
			sourceWriter.outdent();
			sourceWriter.println("}");
			if (returnType.equals(stringType))
			{
				sourceWriter.println("else if (w instanceof HasText) {");
				sourceWriter.indent();
				sourceWriter.println("return ((HasText)w).getText();");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			sourceWriter.outdent();
			sourceWriter.println("}");
			
			sourceWriter.println("return null;");
		}
		else
		{
			sourceWriter.println("return _"+methodName+";");
		}
		
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	private void generateScreenGetterMethod(SourceWriter srcWriter)
	{
		srcWriter.println("public IsWidget _getFromScreen(String widgetName){");
		srcWriter.indent();
		srcWriter.println("IsWidget ret = Screen.get(widgetName);");
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
	
	private void checkMethodSignature(JMethod method)
    {
		JType returnType = method.getReturnType();
		String name = method.getName();
		if (name.startsWith("get"))
		{
			if (method.getParameters().length != 0)
			{
				throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
			}
			if (!JClassUtils.isSimpleType(returnType))
			{
				JClassType returnTypeClass = returnType.isClassOrInterface();
				if (returnTypeClass == null || !returnTypeClass.isAssignableTo(screenBinderType))
				{
					throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
				}
			}
		}
		else if (name.startsWith("set"))
		{
			if (method.getParameters().length != 1)
			{
				throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
			}
			
		    if (returnType.getErasedType() != JPrimitiveType.VOID)
		    {
				throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
		    }
		    JType parameterType = method.getParameterTypes()[0];
			if (!JClassUtils.isSimpleType(parameterType))
			{
				JClassType parameterTypeClass = parameterType.isClassOrInterface();
				if (parameterTypeClass == null || !parameterTypeClass.isAssignableTo(screenBinderType))
				{
					throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
				}
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+name+"] from ScreenBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
		}
    }
}
