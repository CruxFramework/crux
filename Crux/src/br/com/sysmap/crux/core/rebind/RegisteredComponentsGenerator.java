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
import java.util.Map;

import br.com.sysmap.crux.core.rebind.screen.Component;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.config.ComponentConfig;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class RegisteredComponentsGenerator extends AbstractRegisteredElementsGenerator
{
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, Screen screen) 
	{
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.component.RegisteredComponents");
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		generateCreateMethod(sourceWriter, screen);

		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}
	
	protected void generateCreateMethod(SourceWriter sourceWriter, Screen screen) 
	{
		sourceWriter.println("public Component createComponent(String id, com.google.gwt.dom.client.Element element) throws InterfaceConfigException{ ");
		sourceWriter.println("String componentName = element.getAttribute(\"_type\");");
		
		Iterator<Component> iterator = screen.iterateComponents();
		boolean first = true;
		Map<String, Boolean> added = new HashMap<String, Boolean>();
		while (iterator.hasNext())
		{
			Component component = iterator.next();
			generateCreateComponentBlock(sourceWriter, component, added, first);
			first = false;
		}
		sourceWriter.println("throw new InterfaceConfigException(\""+messages.errorGeneratingRegisteredComponentsNotRegistered()+" \" +componentName);");
		sourceWriter.println("}");
	} 
	
	protected void generateCreateComponentBlock(SourceWriter sourceWriter, Component component, Map<String, Boolean> added, boolean first)
	{
		String type = component.getType();
		if (!added.containsKey(type))
		{
			if (!first)
			{
				sourceWriter.print("else ");
			}
			sourceWriter.println("if (\""+type+"\".equals(componentName)) {");
			sourceWriter.println("return new "+ComponentConfig.getClientClass(type)+"("+ComponentConfig.getClientConstructorParams(type)+");");
			sourceWriter.println("}");
			added.put(type, true);
		}
	}
}
