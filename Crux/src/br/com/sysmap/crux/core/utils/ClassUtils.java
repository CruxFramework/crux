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
	public static boolean hasValidSetter(JClassType widgetType, String setterMethod, JType attrType)
	{
		if (widgetType.findMethod(setterMethod, new JType[]{attrType}) != null)
		{
			return true;
		}
		if (attrType.isPrimitive() != null)
		{
			JClassType wrapperType = widgetType.getOracle().findType(attrType.isPrimitive().getQualifiedBoxedSourceName());
			if (widgetType.findMethod(setterMethod, new JType[]{wrapperType}) != null)
			{
				return true;
			}
		}
		else
		{
			JPrimitiveType primitiveType = getPrimitiveFromWrapper(attrType);
			if (primitiveType != null && widgetType.findMethod(setterMethod, new JType[]{primitiveType}) != null)
			{
				return true;
			}
		}
		if (widgetType.getSuperclass() != null)
		{
			return hasValidSetter(widgetType.getSuperclass(), setterMethod, attrType);
		}
		return false;
	}

	/**
	 * @param attrType
	 * @return
	 */
	private static JPrimitiveType getPrimitiveFromWrapper(JType attrType)
    {
		if (attrType.getQualifiedSourceName().equals(JPrimitiveType.INT.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.INT;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.SHORT.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.SHORT;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.LONG.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.LONG;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.BYTE.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.BYTE;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.FLOAT.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.FLOAT;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.DOUBLE.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.DOUBLE;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.BOOLEAN.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.BOOLEAN;
		}
		else if (attrType.getQualifiedSourceName().equals(JPrimitiveType.CHAR.getQualifiedBoxedSourceName()))
		{
			return JPrimitiveType.CHAR;
		}
	    return null;
    }

	/**
	 * @param methodName
	 * @return
	 */
	public static JClassType getReturnTypeFromMethodClass(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = getMethod(clazz, methodName, params);
		
		if (method == null)
		{
			return null;
		}
		JType returnType = method.getReturnType();
		if (!(returnType instanceof JClassType))
		{
			return null;
		}
		return (JClassType) returnType;
    }

	/**
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 */
	public static JMethod getMethod(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = null;
	    JClassType superClass = clazz;
	    while (method == null && superClass.getSuperclass() != null)
	    {
	    	method = superClass.findMethod(methodName, params);
	    	superClass = superClass.getSuperclass();
	    }
	    return method;
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
		if (expectedType == JPrimitiveType.INT || Integer.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Integer.parseInt("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.SHORT || Short.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Short.parseShort("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.LONG || Long.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Long.parseLong("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BYTE || Byte.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Byte.parseByte("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.FLOAT || Float.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Float.parseFloat("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.DOUBLE || Double.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Double.parseDouble("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Boolean.parseBoolean("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.CHAR || Character.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return valueVariable+".charAt(0)";
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
