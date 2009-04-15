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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

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
	 * Generate the code for test if serialisation must be done. Avoid NullPointerExceptions
	 * @param sourceWriter
	 */
	@Override
	protected void generateTestForNullChecking(SourceWriter sourceWriter)
	{
		sourceWriter.print("(jsonValue != null && jsonValue.isObject() != null)");
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
		JSONParser.getInstance().createGenericScope();
		
		sourceWriter.print("{");

		sourceWriter.print("com.google.gwt.json.client.JSONObject jO"+objName+"_o = jsonValue.isObject();");

		Class<?> objectClass;
		if (parameterType instanceof ParameterizedType)
		{
			objectClass = (Class<?>) ((ParameterizedType)parameterType).getRawType();
		}
		else
		{
			objectClass = (Class<?>)parameterType;
		}

		TypeVariable<?>[] typeParameters = objectClass.getTypeParameters();

		for (int i=0; i<typeParameters.length; i++) 
		{
			TypeVariable<?> typeVariable = typeParameters[i];
			if (parameterType instanceof ParameterizedType &&
				((ParameterizedType)parameterType).getActualTypeArguments().length > 0)
			{
				JSONParser.getInstance().setGenericTypeInfo(typeVariable.getName(), ((ParameterizedType)parameterType).getActualTypeArguments()[i]);
			}
		}

		Map<String, Type> properties = describe(objectClass);

		for (String property : properties.keySet()) 
		{
			sourceWriter.print("jsonValue=jO"+objName+"_o.get(\""+property+"\");");
			Type propertyType = properties.get(property);
			if (propertyType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType)propertyType;
				JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
			}
			else if (propertyType instanceof GenericArrayType)
			{
				JSONArrayParser.getInstance().generateArrayDeclaration((GenericArrayType)propertyType, sourceWriter);
			}
			else
			{
				Class<?> param = (Class<?>) propertyType;
				if (param.isArray())
				{
					JSONArrayParser.getInstance().generateArrayDeclaration(param, sourceWriter);
				}
				else
				{
					sourceWriter.print(param.getName());
				}
			}

			sourceWriter.print(" "+objName+"_o"+property+"=");
			JSONParser.getInstance().generateParameterDeserialisationForType(propertyType, sourceWriter, objName+"_o"+property);
			sourceWriter.print(";");
			String prop = Character.toUpperCase(property.charAt(0)) + property.substring(1);
			sourceWriter.print(objName+".set"+prop+"("+objName+"_o"+property+");");
		}
		sourceWriter.print("}");
		
		JSONParser.getInstance().destroyGenericScope();
	}

	/**
	 * Return a map with writable properties for specified class
	 * @param objectClass
	 * @return
	 */
	private Map<String, Type> describe(Class<?> objectClass) 
	{
		Map<String, Type> properties = new HashMap<String, Type>();
		for (Method method : objectClass.getMethods()) 
		{
			if (method.getParameterTypes().length == 1 && method.getName().startsWith("set"))
			{
				String methodName = method.getName().substring(3);
				methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
				properties.put(methodName, method.getGenericParameterTypes()[0]);
			}
		}

		return properties;
	} 
}
