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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.sysmap.crux.core.client.screen.RegisteredWidgetFactories;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.screen.Widget;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class RegisteredWidgetFactoriesGenerator extends AbstractRegisteredElementsGenerator
{
	/**
	 * 
	 */
	@Override
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, List<Screen> screens) 
	{
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImplementedInterface(RegisteredWidgetFactories.class.getName());
		composer.addImport(com.google.gwt.user.client.ui.Widget.class.getName());
		composer.addImport(GWT.class.getName());
		
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);
		sourceWriter.println("private java.util.Map<String, WidgetFactory<? extends Widget>> widgetFactories = new java.util.HashMap<String, WidgetFactory<? extends Widget>>();");

		generateConstructor(logger, sourceWriter, screens, implClassName);

		sourceWriter.println("public WidgetFactory<? extends Widget> getWidgetFactory(String type) throws InterfaceConfigException{ ");
		sourceWriter.println("if (!widgetFactories.containsKey(type)) {");
		sourceWriter.println("throw new InterfaceConfigException(\""+messages.errorGeneratingRegisteredWidgetFactoryNotRegistered()+" \" +type);");
		sourceWriter.println("}");
		sourceWriter.println("return widgetFactories.get(type);");
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param screens
	 * @param implClassName
	 */
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, List<Screen> screens, String implClassName) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		
		Map<String, Boolean> added = new HashMap<String, Boolean>();

		if (Environment.isProduction() || !Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableHotDeploymentForWidgetFactories()))
		{
			for (Screen screen : screens)
			{
				Iterator<Widget> iterator = screen.iterateWidgets();
				while (iterator.hasNext())
				{
					Widget widget = iterator.next();
					generateCreateWidgetBlock(logger, sourceWriter, widget, added);
				}
			}
		}
		else
		{
			generateCreateWidgetBlockForAllWidgets(logger, sourceWriter);
		}
		sourceWriter.println("}");
	} 
	
	/**
	 * @param logger
	 * @param sourceWriter
	 */
	protected void generateCreateWidgetBlockForAllWidgets(TreeLogger logger, SourceWriter sourceWriter)
	{
		Set<String> libraries = WidgetConfig.getRegisteredLibraries();
		
		for (String library : libraries)
		{
			Set<String> registeredLibraryFactories = WidgetConfig.getRegisteredLibraryFactories(library);
			for (String factory : registeredLibraryFactories)
			{
				String type = library+"_"+factory;
				Class<? extends WidgetFactory<?>> widgetClass = WidgetConfig.getClientClass(type);
				sourceWriter.println("widgetFactories.put(\""+type+"\", (WidgetFactory<? extends Widget>)GWT.create("+getClassSourceName(widgetClass)+".class));");
			}
		}
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param widget
	 * @param added
	 */
	protected void generateCreateWidgetBlock(TreeLogger logger, SourceWriter sourceWriter, Widget widget, Map<String, Boolean> added)
	{
		String type = widget.getType();
		if (!added.containsKey(type))
		{
			Class<? extends WidgetFactory<?>> widgetClass = WidgetConfig.getClientClass(type);
			if (widgetClass != null)
			{
				sourceWriter.println("widgetFactories.put(\""+type+"\", (WidgetFactory<? extends Widget>)GWT.create("+getClassSourceName(widgetClass)+".class));");
				added.put(type, true);
			}
			else
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredWidgetFactoryNotRegistered()+type);
			}
		}
	}
}
