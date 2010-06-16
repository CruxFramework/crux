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

import java.io.PrintWriter;
import java.lang.reflect.Method;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractInterfaceWrapperGenerator extends AbstractGenerator
{
	/**
	 * 
	 */
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		try 
		{
			TypeOracle typeOracle = context.getTypeOracle(); 
			JClassType classType = typeOracle.getType(typeName);
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName() + "Impl";
			generateClass(logger, context, classType, packageName, className);
			return packageName + "." + className;
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingScreenWrapper(e.getLocalizedMessage()), e);
			throw new UnableToCompleteException();
		}
	}

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param classType
	 * @param packageName
	 * @param className
	 * @throws ClassNotFoundException
	 * @throws WrapperGeneratorException
	 */
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, String packageName, String className) throws ClassNotFoundException, WrapperGeneratorException 
	{
		PrintWriter printWriter = context.tryCreate(logger, packageName, className);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
		composer.addImplementedInterface(classType.getName());
		composer.addImport(Screen.class.getName());
		composer.addImport(GWT.class.getName());
		composer.addImport(Crux.class.getName());
		composer.addImport(Window.class.getName());
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		Class<?> interfaceClass = Class.forName(getClassBinaryName(classType));
		generateMethodWrappers(logger, interfaceClass, sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}

	/**
	 * 
	 * @param logger
	 * @param interfaceClass
	 * @param sourceWriter
	 * @throws WrapperGeneratorException
	 */
	private void generateMethodWrappers(TreeLogger logger, Class<?> interfaceClass, SourceWriter sourceWriter) throws WrapperGeneratorException
	{
		for (Method method : interfaceClass.getMethods())
		{
			generateMethodWrapper(logger, method, sourceWriter);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param method
	 * @param sourceWriter
	 * @throws WrapperGeneratorException
	 */
	protected abstract void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws WrapperGeneratorException;
	
	/**
	 * 
	 * @param parameterClass
	 * @return
	 */
	protected String getClassNameForPrimitive(Class<?> parameterClass)
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
	
	/**
	 * 
	 * @param declaringClass
	 * @return
	 */
	@SuppressWarnings("deprecation")
    protected String getControllerName(Class<?> declaringClass)
	{
		br.com.sysmap.crux.core.client.controller.ControllerName annotation = declaringClass.getAnnotation(br.com.sysmap.crux.core.client.controller.ControllerName.class);
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
}
