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
package br.com.sysmap.crux.core.utils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;

import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassUtils
{
	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public static String getSetterMethod(String propertyName)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = "set"+Character.toUpperCase(propertyName.charAt(0)); 
		if (propertyName.length() > 1)
		{
			result += propertyName.substring(1);
		}
		return result;
	}

	/**
	 * 
	 * @param propertyName
	 * @param baseClass 
	 * @return
	 */
	public static String getGetterMethod(String propertyName, JClassType baseClass)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = ""+Character.toUpperCase(propertyName.charAt(0)); 
		result += propertyName.substring(1);
		if (propertyName.length() > 1)
		{
			try
            {
	            baseClass.getMethod("get"+result, new JType[]{});
                result = "get"+result;
            }
            catch (Exception e)
            {
	            try
                {
	                baseClass.getMethod("is"+result, new JType[]{});
	                result = "is"+result;
                }
                catch (Exception e1)
                {
                	if (baseClass.getSuperclass() == null)
                	{
                		result = null;
                	}
                	else
                	{
                		result = getGetterMethod(propertyName, baseClass.getSuperclass());
                	}
                }
            }
			
		}
		return result;
	}

	/**
	 * 
	 * @param widgetType
	 * @param setterMethod
	 * @return
	 */
	public static boolean hasValidSetter(Class<?> widgetType, String setterMethod, Class<?> attrType)
	{
		try
		{
			widgetType.getMethod(setterMethod, new Class[]{attrType});
			return true;
		}
		catch (Exception e)
		{
			try
			{
				Class<?> primitieTypeEquivalent = getReflectionEquivalentTypeForPrimities(attrType);
				if (primitieTypeEquivalent != null)
				{
					widgetType.getMethod(setterMethod, new Class[]{primitieTypeEquivalent});
					return true;
				}
				return false;
			}
			catch (Exception e1)
			{
				return false;
			}
		}	
	}
	
	/**
	 * @param jPrimitiveType
	 * @return
	 */
	public static Class<?> getReflectionEquivalentTypeForPrimities(Class<?> jPrimitiveType)
	{
		if (jPrimitiveType.equals(Integer.class))
		{
			return Integer.TYPE;
		}
		else if (jPrimitiveType.equals(Integer.TYPE))
		{
			return Integer.class;
		}
		else if (jPrimitiveType.equals(Short.class))
		{
			return Short.TYPE;
		}
		else if (jPrimitiveType.equals(Short.TYPE))
		{
			return Short.class;
		}
		else if (jPrimitiveType.equals(Long.class))
		{
			return Long.TYPE;
		}
		else if (jPrimitiveType.equals(Long.TYPE))
		{
			return Long.class;
		}
		else if (jPrimitiveType.equals(Byte.class))
		{
			return Byte.TYPE;
		}
		else if (jPrimitiveType.equals(Byte.TYPE))
		{
			return Byte.class;
		}
		else if (jPrimitiveType.equals(Float.class))
		{
			return Float.TYPE;
		}
		else if (jPrimitiveType.equals(Float.TYPE))
		{
			return Float.class;
		}
		else if (jPrimitiveType.equals(Double.class))
		{
			return Double.TYPE;
		}
		else if (jPrimitiveType.equals(Double.TYPE))
		{
			return Double.class;
		}
		else if (jPrimitiveType.equals(Boolean.class))
		{
			return Boolean.TYPE;
		}
		else if (jPrimitiveType.equals(Boolean.TYPE))
		{
			return Boolean.class;
		}
		else if (jPrimitiveType.equals(Character.class))
		{
			return Character.TYPE;
		}
		else if (jPrimitiveType.equals(Character.TYPE))
		{
			return Character.class;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * @param classType
	 * @return
	 */
	public static String getSourceName(JClassType classType)
	{
		String packageName = classType.getPackage().getName();
		if (packageName == null)
		{
			packageName = "";
		}
		String className = classType.getQualifiedBinaryName().substring(packageName.length()+1);
		className = className.replace('$', '_');
		
		return className;
	}
	
	/**
	 * 
	 * @param valueVariable
	 * @param expectedType
	 * @return
	 * @throws NotFoundException 
	 */
	public static String getParsingExpressionForSimpleType(String valueVariable, JType expectedType) throws NotFoundException
	{
		if (expectedType.isPrimitive() != null)
		{	
			JPrimitiveType primitiveType = expectedType.isPrimitive();
			if (primitiveType.equals(JPrimitiveType.INT))
			{
				return "Integer.parseInt("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.SHORT))
			{
				return "Short.parseShort("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.LONG))
			{
				return "Long.parseLong("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.BYTE))
			{
				return "Byte.parseByte("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.FLOAT))
			{
				return "Float.parseFloat("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.DOUBLE))
			{
				return "Double.parseDouble("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.BOOLEAN))
			{
				return "Boolean.parseBoolean("+valueVariable+")";
			}
			else if (primitiveType.equals(JPrimitiveType.CHAR))
			{
				return valueVariable+".charAt(0)";
			}
		}
		else if (expectedType.isEnum() != null)
		{
			return expectedType.getQualifiedSourceName()+".valueOf("+valueVariable+")";
		}
		else
		{
			JClassType stringType = ((JClassType)expectedType).getOracle().getType(String.class.getName());
		    if (stringType.isAssignableFrom((JClassType)expectedType))
		    {
		    	return valueVariable;
		    }
			
		}

		return null;
	}

	/**
	 * 
	 * @param valueVariable
	 * @param expectedType
	 * @return
	 */
	public static String getParsingExpressionForSimpleType(String valueVariable, Class<?> expectedType)
	{
	    if (expectedType.equals(String.class))
	    {
	    	return valueVariable;
	    }
		else if (expectedType.equals(Integer.class) || expectedType.equals(Integer.TYPE))
		{
			return "Integer.parseInt("+valueVariable+")";
		}
		else if (expectedType.equals(Short.class) || expectedType.equals(Short.TYPE))
		{
			return "Short.parseShort("+valueVariable+")";
		}
		else if (expectedType.equals(Long.class) || expectedType.equals(Long.TYPE))
		{
			return "Long.parseLong("+valueVariable+")";
		}
		else if (expectedType.equals(Byte.class) || expectedType.equals(Byte.TYPE))
		{
			return "Byte.parseByte("+valueVariable+")";
		}
		else if (expectedType.equals(Float.class) || expectedType.equals(Float.TYPE))
		{
			return "Float.parseFloat("+valueVariable+")";
		}
		else if (expectedType.equals(Double.class) || expectedType.equals(Double.TYPE))
		{
			return "Double.parseDouble("+valueVariable+")";
		}
		else if (expectedType.equals(Boolean.class) || expectedType.equals(Boolean.TYPE))
		{
			return "Boolean.parseBoolean("+valueVariable+")";
		}
		else if (expectedType.equals(Character.class) || expectedType.equals(Character.TYPE))
		{
			return valueVariable+".charAt(0)";
		}
		else if (expectedType.isEnum())
		{
			return getClassSourceName(expectedType)+".valueOf("+valueVariable+")";
		}

		return null;
	}

	/**
	 * 
	 * @param handlerClass
	 * @return
	 */
	public static String getClassSourceName(Class<?> handlerClass)
	{
		String sourceName = handlerClass.getName();
		sourceName = sourceName.replace('$','.');
		return sourceName;
	}
	
	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	public static TagChildAttributes getChildtrenAttributesAnnotation(Class<?> processorClass)
	{
		TagChildAttributes attributes = processorClass.getAnnotation(TagChildAttributes.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && !superClass.equals(WidgetFactory.class))
			{
				attributes = getChildtrenAttributesAnnotation(superClass);
			}
		}
		
		return attributes;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
	{
		try
		{
			return clazz.getMethod(name, parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			return getProtectedMethod(clazz, name, parameterTypes);
		}
	}

	/**
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static JField getDeclaredField(JClassType clazz, String name) throws NoSuchFieldException
	{
		JField field = clazz.getField(name);
		if (field == null)
		{
			if (clazz.getSuperclass() == null)
			{
				throw new NoSuchFieldException(name);
			}
			field = getDeclaredField(clazz.getSuperclass(), name);
		}

		return field;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public static JField[] getDeclaredFields(JClassType clazz)
	{
		if (clazz.getSuperclass() == null)
		{
			return new JField[0];
		}
		Set<JField> result = new HashSet<JField>();
		JField[] declaredFields = clazz.getFields();
		for (JField field : declaredFields)
		{
			result.add(field);
		}
		clazz = clazz.getSuperclass();
		while (clazz.getSuperclass() != null)
		{
			declaredFields = clazz.getFields();
			for (JField field : declaredFields)
			{
				if (!result.contains(field))
				{
					result.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		return result.toArray(new JField[result.size()]);
	}
	
	/**
	 * @param method
	 * @return
	 */
	public static String getMethodDescription(Method method)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(ClassUtils.getClassSourceName(method.getDeclaringClass()));
		str.append(".");
		str.append(method.getName());
		str.append("(");
		boolean needsComma = false;
		
		for ( Class<?> type : method.getParameterTypes())
		{
			if (needsComma)
			{
				str.append(",");
			}
			needsComma = true;
			str.append(ClassUtils.getClassSourceName(type));
		}
		str.append(")");
		
		return str.toString();
	}
	
	/**
	 * @param method
	 * @return
	 */
	public static String getMethodDescription(JMethod method)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(method.getEnclosingType().getQualifiedSourceName());
		str.append(".");
		str.append(method.getName());
		str.append("(");
		boolean needsComma = false;
		
		for (JParameter parameter: method.getParameters())
		{
			if (needsComma)
			{
				str.append(",");
			}
			needsComma = true;
			str.append(parameter.getType().getParameterizedQualifiedSourceName());
		}
		str.append(")");
		
		return str.toString();
	}
	
	/**
	 * 
	 * @param clazz
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	protected static Method getProtectedMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
	{
		try
		{
			Method method = clazz.getDeclaredMethod(name, parameterTypes);
			if (Modifier.isProtected(method.getModifiers()))
			{
				return method;
			}
		}
		catch (NoSuchMethodException e)
		{
			if (!clazz.equals(Object.class))
			{
				return getProtectedMethod(clazz.getSuperclass(), name, parameterTypes);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param parameterType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTypeDeclaration(Type parameterType)
	{
		StringBuilder result = new StringBuilder();
		if (parameterType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType =((ParameterizedType)parameterType);
			result.append(getTypeDeclaration(parameterizedType.getRawType()));
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments != null && actualTypeArguments.length > 0)
			{
				result.append("<");
				for (Type type : actualTypeArguments)
				{
					result.append(getTypeDeclaration(type));
				}
				result.append(">");
			}
			
		}
		else if (parameterType instanceof GenericArrayType)
		{
			GenericArrayType genericArrayType = (GenericArrayType) parameterType;
			result.append(getTypeDeclaration(genericArrayType.getGenericComponentType()));
			result.append("[]");
		}
		else if (parameterType instanceof TypeVariable)
		{
			TypeVariable<GenericDeclaration> typeVariable = (TypeVariable<GenericDeclaration>) parameterType;
			result.append(typeVariable.getName());
			GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
			if (genericDeclaration != null)
			{
				TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
				if (typeParameters != null && typeParameters.length > 0)
				{
					result.append("<");
					for (Type type : typeParameters)
					{
						result.append(getTypeDeclaration(type));
					}
					result.append(">");
				}
			}
		}
		else if (parameterType instanceof Class)
		{
			Class<?> parameterClass = ((Class<?>)parameterType);
			if (parameterClass.isArray())
			{
				Class<?> componentType = parameterClass.getComponentType();
				result.append(getTypeDeclaration(componentType));
				int numDim = getArrayDimensions(parameterClass);
				for (int i=0; i<numDim; i++)
				{
					result.append("[]");
				}
			}
			else
			{
				result.append(getClassSourceName(parameterClass));
			}
		}
		else if (parameterType instanceof WildcardType)
		{
			result.append("?");
		}
		return result.toString();
	}
	
	/**
	 * 
	 * @param parameterClass
	 * @return
	 */
	private static int getArrayDimensions(Class<?> parameterClass)
	{
		String name = getClassSourceName(parameterClass);
		for (int i=0; i<name.length(); i++)
		{
			if (name.charAt(i) != '[')
			{
				return i;
			}
		}
		return 0;
	}	
}
