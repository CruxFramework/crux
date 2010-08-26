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
package br.com.sysmap.crux.core.rebind.i18n;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.sysmap.crux.core.client.i18n.DeclaredI18NMessages;
import br.com.sysmap.crux.core.i18n.MessageClasses;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.screen.Screen;
import br.com.sysmap.crux.core.rebind.scanner.screen.Widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredI18NMessagesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, Set<String>> declaredMessages = new HashMap<String, Set<String>>();
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredI18NMessagesProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(DeclaredI18NMessages.class.getCanonicalName()));
    }	
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		GWT.class.getCanonicalName(), 
			};
		    return imports;
    }

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public "+getProxySimpleName()+"(){ ");
		srcWriter.indent();

		List<Screen> screens = getScreens();
		for (Screen screen : screens)
		{
			Iterator<Widget> iterator = screen.iterateWidgets();
			while (iterator.hasNext())
			{
				Widget widget = iterator.next();
				getMessageBlock(widget);
			}
			getMessageBlockForProperty(screen.getTitle());
		}
		generateMessagesBlockPopulation(srcWriter);
		
		srcWriter.outdent();
		srcWriter.println("}");
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private java.util.Map<String, String> messages = new java.util.HashMap<String, String>();");
    }

	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateGetMessageMethod(srcWriter);
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }	
	
	/**
	 * @param srcWriter
	 */
	private void generateGetMessageMethod(SourceWriter srcWriter)
    {
		srcWriter.println("public String getMessage(String key) { ");
		srcWriter.indent();
		srcWriter.println("if (!messages.containsKey(key)) {");
		srcWriter.indent();
		srcWriter.println("return key;");
		srcWriter.outdent();
		srcWriter.println("}");
		srcWriter.println("return messages.get(key);");
		srcWriter.outdent();
		srcWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 */
	protected void generateMessagesBlockPopulation(SourceWriter sourceWriter)
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
	 * @param widget
	 */
	protected void getMessageBlock(Widget widget)
	{
		Iterator<String> iterator = widget.iterateProperties();
		while (iterator.hasNext())
		{
			String property = (String) iterator.next();
			getMessageBlockForProperty(property);
		}
	}

	/**
	 * @param logger
	 * @param declaredMessages
	 * @param text
	 */
	protected void getMessageBlockForProperty(String text)
	{
		if (text != null && isKeyReference(text))
		{
			String[] messageParts = getKeyMessageParts(text);

			String messagesClassName = MessageClasses.getMessageClass(messageParts[0]);
			if (messagesClassName == null)
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingDeclaredMessagesClassNotFound(messageParts[0]));
			}
			else
			{
				if (!declaredMessages.containsKey(messagesClassName))
				{
					declaredMessages.put(messagesClassName, new HashSet<String>());
				}
				Set<String> messages = declaredMessages.get(messagesClassName);
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
