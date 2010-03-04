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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.sysmap.crux.core.i18n.MessageClasses;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.Widget;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class DeclaredI18NMessagesGenerator extends AbstractRegisteredElementsGenerator
{
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractRegisteredElementsGenerator#generateClass(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext, com.google.gwt.core.ext.typeinfo.JClassType, java.util.List)
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
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.i18n.DeclaredI18NMessages");
		composer.addImport("com.google.gwt.core.client.GWT");
		
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);
		sourceWriter.println("private java.util.Map<String, String> messages = new java.util.HashMap<String, String>();");

		generateConstructor(logger, sourceWriter, screens, implClassName);

		sourceWriter.println("public String getMessage(String key) { ");
		sourceWriter.println("if (!messages.containsKey(key)) {");
		sourceWriter.println("return key;");
		sourceWriter.println("}");
		sourceWriter.println("return messages.get(key);");
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param screens
	 * @param implClassName
	 */
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, List<Screen> screens, String implClassName) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		
		Map<String, Set<String>> declaredMessages = new HashMap<String, Set<String>>();

		for (Screen screen : screens)
		{
			Iterator<Widget> iterator = screen.iterateWidgets();
			while (iterator.hasNext())
			{
				Widget widget = iterator.next();
				getMessageBlock(logger, widget, declaredMessages);
			}
			getMessageBlockForProperty(logger, declaredMessages, screen.getTitle());
		}
		generateMessagesBlockPopulation(logger, sourceWriter, declaredMessages);
		
		sourceWriter.println("}");
	} 

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param declaredMessages
	 */
	protected void generateMessagesBlockPopulation(TreeLogger logger, SourceWriter sourceWriter, Map<String, Set<String>> declaredMessages)
	{
		for (String classSourceName : declaredMessages.keySet())
		{
			String varName = classSourceName.replace('.', '$');
			sourceWriter.println(classSourceName+" "+varName+"=GWT.create("+classSourceName+".class);" );

			Set<String> messages = declaredMessages.get(classSourceName);
			for (String text : messages)
			{
				String[] messageParts = getKeyMessageParts(text);
				sourceWriter.println("messages.put(\"${"+messageParts[0]+"."+messageParts[1]+"}\", "+varName+"."+messageParts[1]+"());");
			}
		}
	}
	
	/**
	 * @param logger
	 * @param widget
	 * @param declaredMessages
	 */
	protected void getMessageBlock(TreeLogger logger, Widget widget, Map<String, Set<String>> declaredMessages)
	{
		Iterator<String> iterator = widget.iterateProperties();
		while (iterator.hasNext())
		{
			String property = (String) iterator.next();
			getMessageBlockForProperty(logger, declaredMessages, property);
		}
	}

	/**
	 * @param logger
	 * @param declaredMessages
	 * @param text
	 */
	protected void getMessageBlockForProperty(TreeLogger logger, Map<String, Set<String>> declaredMessages, String text)
	{
		if (text != null && isKeyReference(text))
		{
			String[] messageParts = getKeyMessageParts(text);

			Class<?> messagesClass = MessageClasses.getMessageClass(messageParts[0]);
			if (messagesClass == null)
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingDeclaredMessagesClassNotFound(messageParts[0]));
			}
			else
			{
				String classSourceName = getClassSourceName(messagesClass);

				if (!declaredMessages.containsKey(classSourceName))
				{
					declaredMessages.put(classSourceName, new HashSet<String>());
				}
				Set<String> messages = declaredMessages.get(classSourceName);
				messages.add(text);
			}
		}
	}

	/**
	 * @param text
	 * @return
	 */
	protected boolean isKeyReference(String text)
	{
		return text.matches("\\$\\{\\w+\\.\\w+\\}");
	}
	
	/**
	 * @param text
	 * @return
	 */
	protected String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}
}
