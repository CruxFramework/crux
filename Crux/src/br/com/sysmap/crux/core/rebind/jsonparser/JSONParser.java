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
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class JSONParser 
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private Deque<Map<String, Type>> genericTypes = new ArrayDeque<Map<String,Type>>();
	
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
	 * Add a generic type info for a specific generic declaration
	 * @param key
	 * @param value
	 */
	void setGenericTypeInfo(String key, Type value)
	{
		genericTypes.getFirst().put(key, value);
	}
	
	/**
	 * Return the generic type info for a specific generic declaration
	 * @param key
	 * @return
	 */
	Type getGenericTypeInfo(String key)
	{
		for (Map<String, Type> context : genericTypes) 
		{
			if (context.containsKey(key))
			{
				Type type = context.get(key);
				if (type instanceof TypeVariable)
				{
					if (key.equals(((TypeVariable<?>)type).getName()))
					{
						continue;
					}
				}
				
				return context.get(key);
			}
		}
		return null;
	}
	
	/**
	 * Create a new context to sole generic type declarations 
	 */
	void createGenericScope()
	{
		genericTypes.push(new HashMap<String, Type>());		
	}
	
	/**
	 * Destroy the current generic context 
	 */
	void destroyGenericScope()
	{
		genericTypes.remove();		
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
			throw new JSONParserException(e.getLocalizedMessage(), e);
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
			else if (Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType()))
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
			Class<?> param;
			if (paramType instanceof TypeVariable)
			{
				param = getClassForTypeariable((TypeVariable<?>)paramType); 
			}
			else
			{
				param = (Class<?>) paramType;
			}

			if (isPrimitive(param))
			{
				generateDeserialisationForPrimitives(param, sourceWriter);
			}
			else if (CharSequence.class.isAssignableFrom(param))
			{
				generateDeserialisationForCharSequences(param, sourceWriter);
			}
			else if (Date.class.isAssignableFrom(param))
			{
				generateDeserialisationForDates(param, sourceWriter);
			}
			else if (List.class.isAssignableFrom(param))
			{
				JSONListParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
			else if (Set.class.isAssignableFrom(param))
			{
				JSONSetParser.getInstance().generateDeserialisation(paramType, sourceWriter, resultDeserialisedVariable);
			}
			else if (Map.class.isAssignableFrom(param))
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
	 * Return true if the class argument represent a primitive type or a type wrapper class
	 * @param param
	 * @return
	 */
	boolean isPrimitive(Class<?> param) 
	{
		return ((param.isPrimitive()) ||
			    (Number.class.isAssignableFrom(param)) ||
			    (Boolean.class.isAssignableFrom(param)) || 
			    (Character.class.isAssignableFrom(param)));
	}

	/**
	 * Deserialise enum types arguments
	 * 
	 * @param param
	 * @param sourceWriter
	 */
	private void generateDeserialisationForEnum(Class<?> param, SourceWriter sourceWriter) 
	{
		sourceWriter.print("(jsonValue.isString()==null?null:"+param.getName()+".valueOf(jsonValue.isString().stringValue()))");
	}

	/**
	 * Deserialise primitive types arguments
	 * @param parameterType
	 * @param sourceWriter
	 */
	private void generateDeserialisationForPrimitives(Class<?> parameterType, SourceWriter sourceWriter)
	{
		if (parameterType.getName().equals("java.lang.Boolean"))
		{
			sourceWriter.print("(jsonValue.isBoolean()==null?null:new Boolean(jsonValue.isBoolean().booleanValue()))");
		}
		else if (parameterType.getName().equals("java.lang.Character"))
		{
			sourceWriter.print("(jsonValue.isString()==null?null:new Character(jsonValue.isString().stringValue().charAt(0)))");
		}
		else if (parameterType.getName().equals("java.lang.Byte"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Byte(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("java.lang.Short"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Short(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("java.lang.Integer"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Integer(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("java.lang.Long"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Long(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("java.lang.Float"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Float(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("java.lang.Double"))
		{
			sourceWriter.print("(jsonValue.isNumber()==null?null:new Double(jsonValue.isNumber().toString()))");
		}
		else if (parameterType.getName().equals("boolean"))
		{
			sourceWriter.print("new Boolean(jsonValue.isBoolean().booleanValue())");
		}
		else if (parameterType.getName().equals("char"))
		{
			sourceWriter.print("new Character(jsonValue.isString().stringValue().charAt(0))");
		}
		else if (parameterType.getName().equals("byte"))
		{
			sourceWriter.print("new Byte(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("short"))
		{
			sourceWriter.print("new Short(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("int"))
		{
			sourceWriter.print("new Integer(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("long"))
		{
			sourceWriter.print("new Long(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("float"))
		{
			sourceWriter.print("new Float(jsonValue.isNumber().toString())");
		}
		else if (parameterType.getName().equals("double"))
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
			throw new ClassCastException(messages.errorJsonParserCharSequenceNotSupported());
		}
		sourceWriter.print("(jsonValue.isString() == null?null:new "+parameterType.getName()+"(jsonValue.isString().stringValue()))");
	}
	
	/**
	 * Deserialise Strings, StringBuilders and StringBuffers arguments.
	 * @param parameterType
	 * @param sourceWriter
	 */
	private void generateDeserialisationForDates(Class<?> parameterType, SourceWriter sourceWriter)
	{
		if ((!"java.util.Date".equals(parameterType.getName())) &&
			(!"java.sql.Date".equals(parameterType.getName())) &&
			(!"java.sql.Timestamp".equals(parameterType.getName())))
		{
			throw new ClassCastException(messages.errorJsonParserDateNotSupported());
		}
		sourceWriter.print("(jsonValue.isObject() == null?null:new "+parameterType.getName()+
				           "(new Long(jsonValue.isObject().get(\"time\").isNumber().toString())))");
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
			else if (type instanceof TypeVariable)
			{
				Class<?> parameterArgClass = getClassForTypeariable((TypeVariable<?>)type); 
				sourceWriter.print(parameterArgClass.getName());
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
	
	/**
	 * Extracts the bound <code>Type</code> for a given TypeVariable.
	 */
	private Type extractBoundForTypeVariable(TypeVariable<?> typeVariable) 
	{
		Type[] bounds = typeVariable.getBounds();
		if (bounds.length == 0) 
		{
			return Object.class;
		}
		Type bound = bounds[0];
		if (bound instanceof TypeVariable) 
		{
			bound = extractBoundForTypeVariable((TypeVariable<?>) bound);
		}
		return bound;
	}
	
	/**
	 * get class for a typeVariable
	 * @param typeVariable
	 * @return
	 */
	Class<?> getClassForTypeariable(TypeVariable<?> typeVariable)
	{
		Type type = getGenericTypeInfo((typeVariable.getName()));
		while ((type != null) && (type instanceof TypeVariable)) 
		{
			type = getGenericTypeInfo(((TypeVariable<?>)type).getName());
		}

		Class<?> result = (Class<?>) type;
		if (result == null)
		{
			result = (Class<?>) extractBoundForTypeVariable(typeVariable);  
		}
		return result;
	}
}
