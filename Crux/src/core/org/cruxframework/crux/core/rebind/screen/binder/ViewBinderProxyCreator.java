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
import org.cruxframework.crux.core.client.screen.views.Target;
import org.cruxframework.crux.core.client.screen.views.ViewBinder;
import org.cruxframework.crux.core.rebind.AbstractViewBindableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewBinderProxyCreator extends AbstractViewBindableProxyCreator
{
	private final JClassType viewBinderType;
	private final JClassType stringType;

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public ViewBinderProxyCreator(TreeLogger logger, GeneratorContext context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
	    try
        {
	    	viewBinderType = invokerIntf.getOracle().getType(ViewBinder.class.getCanonicalName());
	    	stringType = invokerIntf.getOracle().getType(String.class.getCanonicalName());
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyMethods(srcWriter);
		generateViewGetterMethod(srcWriter);
	}
	
	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
    	super.generateProxyFields(srcWriter);
		JMethod[] methods = baseIntf.getOverridableMethods();
    	for (JMethod method : methods)
    	{
    		String methodName = method.getName();
    		if (method.getName().startsWith("get"))
    		{
    			JClassType returnTypeClass = method.getReturnType().isClassOrInterface();
    			if (returnTypeClass != null && returnTypeClass.isAssignableTo(viewBinderType))
    			{
    				String sourceName = returnTypeClass.getParameterizedQualifiedSourceName();
					srcWriter.println("private "+sourceName+" _"+methodName+" = GWT.create("+sourceName+".class);");
    			}
    		}
    	}
    }

	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
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
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter)
	{
		checkMethodSignature(method);

		String name = method.getName();

		String widgetName;
		Target target = method.getAnnotation(Target.class);
		if (target != null) 
		{
			widgetName = target.value();
		}
		else
		{
			if (name.length() > 3)
			{
				widgetName = ""+Character.toLowerCase(name.charAt(3));
				if (name.length() > 4)
				{
					widgetName += name.substring(4);
				}
			}
			else 
			{
				widgetName = "";
			}
		}
		if (name.startsWith("get"))
		{
			if (widgetName.length() > 0)
			{
				JType returnType = method.getReturnType();
				generateWrapperMethodForGetter(sourceWriter, returnType, name, widgetName);
			}
		}
		else if (name.startsWith("set"))
		{
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
	private void generateWrapperMethodForSetter(SourcePrinter sourceWriter, JType parameterType, String methodName, String widgetName)
    {
		String parameterClassName = JClassUtils.getGenericDeclForType(parameterType);
		sourceWriter.println("public void " + methodName+"("+parameterType.getParameterizedQualifiedSourceName()+" value){");

		if (JClassUtils.isSimpleType(parameterType)) 
		{
			sourceWriter.println("IsWidget w = _getFromView(\""+widgetName+"\");");
			
			sourceWriter.println("if (w != null) {");
			sourceWriter.println("if (w instanceof HasValue) {");
			sourceWriter.println("((HasValue<"+parameterClassName+">)w).setValue(value);");
			sourceWriter.println("}");
			sourceWriter.println("else if (w instanceof HasFormatter) {");
			if (parameterType.isPrimitive() != null)
			{
				sourceWriter.println("((HasFormatter)w).setUnformattedValue(("+parameterClassName+")value);");
			}
			else
			{
				sourceWriter.println("((HasFormatter)w).setUnformattedValue(value);");
			}
			sourceWriter.println("}");
			if (parameterType.equals(stringType))
			{
				sourceWriter.println("else if (w instanceof HasText) {");
				sourceWriter.println("((HasText)w).setText(value);");
				sourceWriter.println("}");
			}
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println("this._"+methodName+" = value;");
		}
		
		sourceWriter.println("}");
    }	

	/**
	 * 
	 * @param sourceWriter
	 * @param returnType
	 * @param methodName
	 * @param widgetName
	 */
	private void generateWrapperMethodForGetter(SourcePrinter sourceWriter, JType returnType, String methodName, String widgetName)
	{
		String returnClassName = JClassUtils.getGenericDeclForType(returnType);
		sourceWriter.println("public "+returnType.getParameterizedQualifiedSourceName()+" " + methodName+"(){");

		if (JClassUtils.isSimpleType(returnType)) 
		{
			sourceWriter.println("IsWidget w = _getFromView(\""+widgetName+"\");");
			
			sourceWriter.println("if (w != null) {");
			sourceWriter.println("if (w instanceof HasValue) {");
			sourceWriter.println("return ((HasValue<"+returnClassName+">)w).getValue();");
			sourceWriter.println("}");
			sourceWriter.println("else if (w instanceof HasFormatter) {");
			sourceWriter.println("return ("+returnClassName+")((HasFormatter)w).getUnformattedValue();");
			sourceWriter.println("}");
			if (returnType.equals(stringType))
			{
				sourceWriter.println("else if (w instanceof HasText) {");
				sourceWriter.println("return ((HasText)w).getText();");
				sourceWriter.println("}");
			}
			sourceWriter.println("}");
			
			sourceWriter.println("return null;");
		}
		else
		{
			sourceWriter.println("return _"+methodName+";");
		}
		
		sourceWriter.println("}");
	}

	private void checkMethodSignature(JMethod method)
    {
		JType returnType = method.getReturnType();
		String name = method.getName();
		if (name.startsWith("get"))
		{
			if (method.getParameters().length != 0)
			{
				throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
			}
			if (!JClassUtils.isSimpleType(returnType))
			{
				JClassType returnTypeClass = returnType.isClassOrInterface();
				if (returnTypeClass == null || !returnTypeClass.isAssignableTo(viewBinderType))
				{
					throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
				}
			}
		}
		else if (name.startsWith("set"))
		{
			if (method.getParameters().length != 1)
			{
				throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
			}
			
		    if (returnType.getErasedType() != JPrimitiveType.VOID)
		    {
				throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
		    }
		    JType parameterType = method.getParameterTypes()[0];
			if (!JClassUtils.isSimpleType(parameterType))
			{
				JClassType parameterTypeClass = parameterType.isClassOrInterface();
				if (parameterTypeClass == null || !parameterTypeClass.isAssignableTo(viewBinderType))
				{
					throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
				}
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+name+"] from ViewBinder ["+baseIntf.getQualifiedSourceName()+"] has an invalid signature.");
		}
    }
}
