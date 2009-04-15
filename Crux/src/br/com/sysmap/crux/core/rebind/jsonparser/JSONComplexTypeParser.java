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
package br.com.sysmap.crux.core.rebind.jsonparser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;

import com.google.gwt.user.rebind.SourceWriter;

/**
 * Helper Class used by Generators to deserialise objects returned by server before they
 * are passed to callback handlers.
 * 
 * @author Thiago Bustamante
 *
 */
public abstract class JSONComplexTypeParser 
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	String defaultClass;

	/**
	 * Deserialise lists, instantiating and population them with json values returned by server.
	 * @param parameterType
	 * @param sourceWriter
	 * @param listName
	 */
	void generateDeserialisation(Type parameterType, SourceWriter sourceWriter, String listName) 
	{
		sourceWriter.print("(");
		generateTestForNullChecking(sourceWriter);
		sourceWriter.print("?");
		generateInstantiation(parameterType, sourceWriter);
		sourceWriter.print(":null)");
		sourceWriter.print(";");
		sourceWriter.print("if (");
		generateTestForNullChecking(sourceWriter);
		sourceWriter.print(")");
		sourceWriter.print("{");
		generatePopulation(parameterType, sourceWriter, listName);
		sourceWriter.print("}");
	}
	
	/**
	 * Generate the code for test if serialisation must be done. Avoid NullPointerExceptions
	 * @param sourceWriter
	 */
	protected abstract void generateTestForNullChecking(SourceWriter sourceWriter);
	
	/**
	 * Generate the code for collection instantiation
	 * @param parameterType
	 * @param sourceWriter
	 */
	protected void generateInstantiation(Type parameterType, SourceWriter sourceWriter)
	{
		sourceWriter.print("new ");
		if (parameterType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType)parameterType;
			if (((Class<?>)parameterizedType.getRawType()).isInterface())
			{
				if (defaultClass != null)
				{
					JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, defaultClass);
				}
				else
				{
					throw new ClassCastException(messages.errorJsonParserPolymorphismNotSupported());
				}
			}
			else
			{
				JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
			}
		}
		else
		{
			Class<?> parameterClass;
			if (parameterType instanceof TypeVariable)
			{
				parameterClass = JSONParser.getInstance().getClassForTypeariable((TypeVariable<?>)parameterType); 
			}
			else
			{
				parameterClass = (Class<?>) parameterType;
			}
			
			if (parameterClass.isInterface())
			{
				if (defaultClass != null)
				{
					sourceWriter.print(defaultClass);
				}
				else
				{
					throw new ClassCastException(messages.errorJsonParserPolymorphismNotSupported());
				}
			}
			else
			{
				sourceWriter.print(parameterClass.getName());
			}
		}
		sourceWriter.print("()");
	} 

	/**
	 * Generate code for populate the collection content.
	 * @param parameterType
	 * @param sourceWriter
	 * @param listName
	 */
	protected abstract void generatePopulation(Type parameterType, SourceWriter sourceWriter, String listName);
}
