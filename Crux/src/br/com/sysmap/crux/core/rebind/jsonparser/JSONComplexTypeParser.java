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
	String defaultClass;

	/**
	 * Deserialise lists, instantiating and population them with json values returned by server.
	 * @param parameterType
	 * @param sourceWriter
	 * @param listName
	 */
	void generateDeserialisation(Type parameterType, SourceWriter sourceWriter, String listName)
	{
		generateInstantiation(parameterType, sourceWriter);
		sourceWriter.print(";");
		generatePopulation(parameterType, sourceWriter, listName);
	}
	
	/**
	 * Generate the code for collection instantiation
	 * @param parameterType
	 * @param sourceWriter
	 */
	private void generateInstantiation(Type parameterType, SourceWriter sourceWriter)
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
					throw new ClassCastException("unsupported type");
					//TODO arrumar mensagem
				}
			}
			else
			{
				JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
			}
		}
		else
		{
			if (((Class<?>)parameterType).isInterface())
			{
				if (defaultClass != null)
				{
					sourceWriter.print(defaultClass);
				}
				else
				{
					throw new ClassCastException("unsupported type");
					//TODO arrumar mensagem
				}
			}
			else
			{
				sourceWriter.print(((Class<?>)parameterType).getName());
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
