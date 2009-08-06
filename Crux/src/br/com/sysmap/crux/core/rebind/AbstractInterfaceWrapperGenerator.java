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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

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
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
	 * @param parameterType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getParameterDeclaration(Type parameterType)
	{
		StringBuilder result = new StringBuilder();
		if (parameterType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType =((ParameterizedType)parameterType);
			result.append(getParameterDeclaration(parameterizedType.getRawType()));
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments != null && actualTypeArguments.length > 0)
			{
				result.append("<");
				for (Type type : actualTypeArguments)
				{
					result.append(getParameterDeclaration(type));
				}
				result.append(">");
			}
			
		}
		else if (parameterType instanceof GenericArrayType)
		{
			GenericArrayType genericArrayType = (GenericArrayType) parameterType;
			result.append(getParameterDeclaration(genericArrayType.getGenericComponentType()));
			result.append("[]");
		}
		else if (parameterType instanceof TypeVariable)
		{
			TypeVariable<GenericDeclaration> typeVariable = (TypeVariable<GenericDeclaration>) parameterType;
			result.append(typeVariable.getName());
			GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
			if (genericDeclaration != null)
			{
				TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
				if (typeParameters != null && typeParameters.length > 0)
				{
					result.append("<");
					for (Type type : typeParameters)
					{
						result.append(getParameterDeclaration(type));
					}
					result.append(">");
				}
				
			}
		}
		else if (parameterType instanceof Class)
		{
			Class<?> parameterClass = ((Class<?>)parameterType);
			if (parameterClass.isArray())
			{
				Class<?> componentType = parameterClass.getComponentType();
				result.append(getParameterDeclaration(componentType));
				int numDim = getArrayDimensions(parameterClass);
				for (int i=0; i<numDim; i++)
				{
					result.append("[]");
				}
			}
			else
			{
				result.append(getClassSourceName(parameterClass));
			}
		}
		else if (parameterType instanceof WildcardType)
		{
			result.append("?");
		}
		return result.toString();
	}

	/**
	 * 
	 * @param parameterClass
	 * @return
	 */
	private int getArrayDimensions(Class<?> parameterClass)
	{
		String name = getClassSourceName(parameterClass);
		for (int i=0; i<name.length(); i++)
		{
			if (name.charAt(i) != '[')
			{
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param logger
	 * @param method
	 * @param sourceWriter
	 * @throws WrapperGeneratorException
	 */
	protected abstract void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws WrapperGeneratorException;
}
