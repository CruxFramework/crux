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

import java.lang.reflect.GenericArrayType;
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
public class JSONObjectParser extends JSONComplexTypeParser
{
	/**
	 * Singleton instance
	 */
	private static JSONObjectParser instance = new JSONObjectParser();
	
	/**
	 * Private constructor
	 */
	private JSONObjectParser() 
	{
		defaultClass = null;
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static JSONObjectParser getInstance()
	{
		return instance;
	}
	
	/**
	 * Generate code for populate the collection content.
	 * @param parameterType
	 * @param sourceWriter
	 * @param objName
	 */
	@Override
	protected void generatePopulation(Type parameterType, SourceWriter sourceWriter, String objName)
	{
		sourceWriter.print("{");

		sourceWriter.print("com.google.gwt.json.client.JSONObject jO"+objName+"_o = jsonValue.isObject();");
		
		sourceWriter.print("for (String str"+objName+"_o: jO"+objName+"_o.keySet())");
		sourceWriter.print("{");
		sourceWriter.print("jsonValue=jO"+objName+"_o.get(str"+objName+"_o);");
		
		Class<?> keyClass = null;
		
		if (parameterType instanceof ParameterizedType)
		{
			// Key
			Type keyArgType = ((ParameterizedType)parameterType).getActualTypeArguments()[0];
			if ((keyArgType instanceof ParameterizedType) 
				||(keyArgType instanceof GenericArrayType)
				||(!(CharSequence.class.isAssignableFrom((Class<?>)keyArgType)) && (!((Class<?>)keyArgType).isPrimitive())))
			{
				throw new ClassCastException("unsupported type");
				//TODO: Arrumar mensagem.
			}
			
			keyClass = ((Class<?>)keyArgType);
			
			// Value
			Type parameterArgType = ((ParameterizedType)parameterType).getActualTypeArguments()[1];
			
			if (parameterArgType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType)parameterArgType;
				JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
			}
			else if (parameterArgType instanceof GenericArrayType)
			{
				JSONArrayParser.getInstance().generateArrayDeclaration((GenericArrayType)parameterArgType, sourceWriter);
			}
			else
			{
				Class<?> param = (Class<?>) parameterArgType;
				if (param.isArray())
				{
					JSONArrayParser.getInstance().generateArrayDeclaration(param, sourceWriter);
				}
				else
				{
					sourceWriter.print(param.getName());
				}
			}
			sourceWriter.print(" "+objName+"_o=");
			JSONParser.getInstance().generateParameterDeserialisationForType(parameterArgType, sourceWriter, objName+"_o");
		}
		else
		{
			keyClass = String.class;
			sourceWriter.print("Object "+objName+"_o=");
			JSONParser.getInstance().generateParameterDeserialisationForType(Object.class, sourceWriter, objName+"_o");
		}
		sourceWriter.print(";");
		sourceWriter.print(objName+".put(new "+keyClass.getName()+"(str"+objName+"_o),"+objName+"_o);");
		sourceWriter.print("}");

		sourceWriter.print("}");
	} 
}
