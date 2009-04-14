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

public class JSONMapParser extends JSONComplexTypeParser
{
	/**
	 * Singleton instance
	 */
	private static JSONMapParser instance = new JSONMapParser();
	
	/**
	 * Private constructor
	 */
	private JSONMapParser() 
	{
		defaultClass = "java.util.HashMap";
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static JSONMapParser getInstance()
	{
		return instance;
	}
	
	/**
	 * Generate code for populate the collection content.
	 * @param parameterType
	 * @param sourceWriter
	 * @param mapName
	 */
	@Override
	protected void generatePopulation(Type parameterType, SourceWriter sourceWriter, String mapName)
	{
		sourceWriter.print("{");

		sourceWriter.print("com.google.gwt.json.client.JSONObject jO"+mapName+"_l = jsonValue.isObject().get(\"map\").isObject();");
		
		sourceWriter.print("for (String str"+mapName+"_l: jO"+mapName+"_l.keySet())");
		sourceWriter.print("{");
		sourceWriter.print("jsonValue=jO"+mapName+"_l.get(str"+mapName+"_l);");
		
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
			sourceWriter.print(" "+mapName+"_l=");
			JSONParser.getInstance().generateParameterDeserialisationForType(parameterArgType, sourceWriter, mapName+"_l");
		}
		else
		{
			keyClass = String.class;
			sourceWriter.print("Object "+mapName+"_l=");
			JSONParser.getInstance().generateParameterDeserialisationForType(Object.class, sourceWriter, mapName+"_l");
		}
		sourceWriter.print(";");
		sourceWriter.print(mapName+".put(new "+keyClass.getName()+"(str"+mapName+"_l),"+mapName+"_l);");
		sourceWriter.print("}");

		sourceWriter.print("}");
	} 
}
