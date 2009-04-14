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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.rebind.SourceWriter;

/**
 * Helper Class used by Generators to deserialise objects returned by server before they
 * are passed to callback handlers.
 * 
 * @author Thiago Bustamante
 *
 */
public class JSONParser 
{
	/**
	 * Singleton instance
	 */
	private static JSONParser instance = new JSONParser();
	
	/**
	 * Private constructor
	 */
	private JSONParser() 
	{
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static JSONParser getInstance()
	{
		return instance;
	}
	
	/**
	 * Generate code block for deserialise the result sent by server to an object that will be passed 
	 * to client callback. The server will serialise the controller result using the json format and 
	 * client callback classes will expect the type returned by the controller.
	 * @param method
	 * @param sourceWriter
	 * @param resultVariable
	 * @param resultDeserialisedVariable
	 */
	public void generateParameterDeserialisationBlock(Method method,
			SourceWriter sourceWriter, String resultVariable, String resultDeserialisedVariable) throws ClassCastException
			{
		try
		{
			// Context variable used by all parsers to recovery the current value been analysed.
			sourceWriter.println("JSONValue jsonValue = "+resultVariable+";");

			Type paramType = method.getGenericParameterTypes()[0];
			if (paramType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType)paramType;
				generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
				sourceWriter.print(" "+resultDeserialisedVariable+"=");
			}
			else
			{
				Class<?> param = (Class<?>) paramType;
				if (param.isArray())
				{
					JSONArrayParser.getInstance().generateArrayDeclaration(param, sourceWriter);
					sourceWriter.print(" "+resultDeserialisedVariable+"=");
				}
				else
				{
					sourceWriter.print(param.getName()+" "+resultDeserialisedVariable+"=");
				}
			}
			generateParameterDeserialisationForType(paramType, sourceWriter, resultDeserialisedVariable);
			sourceWriter.print(";");
		}
		catch (Throwable e)
		{
//TODO arrumar as mensagens aki
			throw new ClassCastException();
		}
	}

	/**
	 * Generate code block for deserialise the result sent by server to an object that will be passed 
	 * to client callback. The server will serialise the controller result using the json format and 
	 * client callback classes will expect the type returned by the controller.
	 * @param paramType
	 * @param sourceWriter
	 * @param resultDeserialisedVariable
	 */
	void generateParameterDeserialisationForType(Type paramType, SourceWriter sourceWriter, String resultDeserialisedVariable)
	{
		if (paramType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType)paramType;
			if (List.class.isAssignableFrom((Class<?>) parameterizedType.getRawType()))
			{
				JSONListParser.getInstance().generateDeserialisation(parameterizedType, sourceWriter, resultDeserialisedVariable);
			}
			else if (Set.class.isAssignableFrom((Class<?>) parameterizedType.getRawType()))
			{
				JSONSetParser.getInstance().generateDeserialisation(parameterizedType, sourceWriter, resultDeserialisedVariable);
			}
			if (Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType()))
			{
				JSONMapParser.getInstance().generateDeserialisation(parameterizedType, sourceWriter, resultDeserialisedVariable);
			}
			else
			{
				JSONObjectParser.getInstance().generateDeserialisation(parameterizedType, sourceWriter, resultDeserialisedVariable);
			}
		}
		else if (paramType instanceof GenericArrayType)
		{
			GenericArrayType genericArrayType = (GenericArrayType) paramType;
			JSONArrayParser.getInstance().generateDeserialisationForArray(genericArrayType, sourceWriter, resultDeserialisedVariable);
		}
		else
		{
			Class<?> param = (Class<?>) paramType;

			if (param.isPrimitive())
			{
				generateDeserialisationForPrimitives(param, sourceWriter);
			}
			else if (CharSequence.class.isAssignableFrom(param))
			{
				generateDeserialisationForCharSequences(param, sourceWriter);
			}
			else if (Date.class.isAssignableFrom(param))
			{

			}
			else if (List.class.isAssignableFrom(param))
			{
				JSONListParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
			else if (Set.class.isAssignableFrom(param))
			{
				JSONSetParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
			if (Map.class.isAssignableFrom(param))
			{
				JSONMapParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
			else if (param.isArray())
			{
				JSONArrayParser.getInstance().generateDeserialisationForArray(param, sourceWriter, resultDeserialisedVariable);
			}
			else if (param.isEnum())
			{
				generateDeserialisationForEnum(param, sourceWriter);
			}
			else
			{
				JSONObjectParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
		}
	}

	/**
	 * Deserialise enum types arguments
	 * 
	 * @param param
	 * @param sourceWriter
	 */
	private void generateDeserialisationForEnum(Class<?> param, SourceWriter sourceWriter) 
	{
		if (!param.isEnum())
		{
			throw new ClassCastException();
			// TODO arrumar mensagem
		}
		
		sourceWriter.print(param.getName()+".valueOf(jsonValue.isString().stringValue())");
	}

	/**
	 * Deserialise primitive types arguments
	 * @param parameterType
	 * @param sourceWriter
	 */
	private void generateDeserialisationForPrimitives(Class<?> parameterType, SourceWriter sourceWriter)
	{
		if (parameterType.getName().equals("java.lang.Boolean") || parameterType.getName().equals("boolean"))
		{
			sourceWriter.print("new Boolean(jsonValue.isBoolean().booleanValue())");
		}
		else if (parameterType.getName().equals("java.lang.Character") || parameterType.getName().equals("char"))
		{
			sourceWriter.print("new Character(jsonValue.isString().stringValue().charAt(0))");
		}
		else if (parameterType.getName().equals("java.lang.Byte") || parameterType.getName().equals("byte"))
		{
			sourceWriter.print("new Byte(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("java.lang.Short") || parameterType.getName().equals("short"))
		{
			sourceWriter.print("new Short(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("java.lang.Integer") || parameterType.getName().equals("int"))
		{
			sourceWriter.print("new Integer(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("java.lang.Long") || parameterType.getName().equals("long"))
		{
			sourceWriter.print("new Long(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("java.lang.Float") || parameterType.getName().equals("float"))
		{
			sourceWriter.print("new Float(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("java.lang.Double") || parameterType.getName().equals("double"))
		{
			sourceWriter.print("new Double(jsonValue.isNumber().toString())");
		}
	}
	
	/**
	 * Deserialise Strings, StringBuilders and StringBuffers arguments.
	 * @param parameterType
	 * @param sourceWriter
	 */
	private void generateDeserialisationForCharSequences(Class<?> parameterType, SourceWriter sourceWriter)
	{
		if ((!"java.lang.String".equals(parameterType.getName())) &&
			(!"java.lang.StringBuilder".equals(parameterType.getName())) &&
			(!"java.lang.StringBuffer".equals(parameterType.getName())))
		{
			throw new ClassCastException("unupported Type");
			//TODO arrumar mensagem
		}
		sourceWriter.print("new "+parameterType.getName()+"(jsonValue.isString().stringValue())");
	}
	
	/**
	 * Recursive Function for builds the parameterized arguments declarations for generics types.
	 * @param parameterizedType
	 * @param sourceWriter
	 * @param baseTypeReplacement
	 */
	void generateDeserialisationForParameterizedTypes(ParameterizedType parameterizedType, 
													SourceWriter sourceWriter, String baseTypeReplacement)
	{
		Type[] parameterArgTypes = parameterizedType.getActualTypeArguments();
		boolean first = true;
		
		if (baseTypeReplacement != null)
		{
			sourceWriter.print(baseTypeReplacement+"<");
		}
		else
		{
			sourceWriter.print(((Class<?>)parameterizedType.getRawType()).getName()+"<");
		}
		for (Type type : parameterArgTypes) 
		{
			if (!first)
			{
				sourceWriter.print(",");
			}
			if (type instanceof ParameterizedType)
			{
				generateDeserialisationForParameterizedTypes((ParameterizedType)type, sourceWriter, null);
			}
			else if (type instanceof GenericArrayType)
			{
				JSONArrayParser.getInstance().generateArrayDeclaration((GenericArrayType)type, sourceWriter);
			}
			else
			{
				Class<?> parameterArgClass = (Class<?>) type;
				sourceWriter.print(parameterArgClass.getName());
			}
			first = false;
		}
		sourceWriter.print(">");
	}
}
