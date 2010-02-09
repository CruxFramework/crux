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

	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, List<Screen> screens, String implClassName) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		
		sourceWriter.println("String msg;");
		Map<String, Boolean> added = new HashMap<String, Boolean>();

		for (Screen screen : screens)
		{
			Iterator<Widget> iterator = screen.iterateWidgets();
			while (iterator.hasNext())
			{
				Widget widget = iterator.next();
				generateGetMessageBlock(logger, sourceWriter, widget, added);
			}
			generateGetMessageBlockForProperty(logger, sourceWriter, added, screen.getTitle());
		}
		sourceWriter.println("}");
	} 
	
	protected void generateGetMessageBlock(TreeLogger logger, SourceWriter sourceWriter, Widget widget, Map<String, Boolean> added)
	{
		Iterator<String> iterator = widget.iterateProperties();
		while (iterator.hasNext())
		{
			String property = (String) iterator.next();
			generateGetMessageBlockForProperty(logger, sourceWriter, added, property);
		}
	}

	protected void generateGetMessageBlockForProperty(TreeLogger logger, SourceWriter sourceWriter, Map<String, Boolean> added, String text)
	{
		if (text != null && isKeyReference(text))
		{
			String[] messageParts = getKeyMessageParts(text);

			if (!added.containsKey(text))
			{
				Class<?> messagesClass = MessageClasses.getMessageClass(messageParts[0]);
				if (messagesClass == null)
				{
					logger.log(TreeLogger.ERROR, messages.errorGeneratingDeclaredMessagesClassNotFound(messageParts[0]));
				}
				else
				{
					String classSourceName = getClassSourceName(messagesClass);
					sourceWriter.println("msg = (("+classSourceName+")GWT.create("+classSourceName+".class))."+messageParts[1]+"();");
					sourceWriter.println("messages.put(\""+text+"\", msg);");
				}
				added.put(text, true);
			}
		}
	}

	protected boolean isKeyReference(String text)
	{
		return text.matches("\\$\\{\\w+\\.\\w+\\}");
	}
	
	protected String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}
}
