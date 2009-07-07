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

import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.i18n.MessagesFactory;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractInterfaceWrapperGenerator extends Generator
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

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

	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, String packageName, String className) throws ClassNotFoundException, ContextGeneratorException 
	{
		PrintWriter printWriter = context.tryCreate(logger, packageName, className);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
		composer.addImplementedInterface(classType.getName());
		composer.addImport(Screen.class.getName());
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		Class<?> interfaceClass = Class.forName(getClassName(classType));
		generateMethodWrappers(logger, interfaceClass, sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}

	protected String getClassName(JClassType classType)
	{
		String pkgName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName();
		String name = classType.getName();
		
		if (name.equals(simpleName))
		{
			return pkgName + "." +name;
		}
		else
		{
			return pkgName + "." + name.substring(0, name.indexOf(simpleName)-1) + "$"+ simpleName;
		}
		
	}

	private void generateMethodWrappers(TreeLogger logger, Class<?> interfaceClass, SourceWriter sourceWriter) throws ContextGeneratorException
	{
		for (Method method : interfaceClass.getMethods())
		{
			generateMethodWrapper(logger, method, sourceWriter);
		}
	}

	protected abstract void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter) throws ContextGeneratorException;
}
