/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso
 * 
 */
public class ClassUtils
{
	private static Set<String> simpleTypes = new HashSet<String>();
	static
	{
		simpleTypes.add(Integer.class.getCanonicalName());
		simpleTypes.add(Short.class.getCanonicalName());
		simpleTypes.add(Byte.class.getCanonicalName());
		simpleTypes.add(Long.class.getCanonicalName());
		simpleTypes.add(Double.class.getCanonicalName());
		simpleTypes.add(Float.class.getCanonicalName());
		simpleTypes.add(Boolean.class.getCanonicalName());
		simpleTypes.add(Character.class.getCanonicalName());
		simpleTypes.add(Integer.TYPE.getCanonicalName());
		simpleTypes.add(Short.TYPE.getCanonicalName());
		simpleTypes.add(Byte.TYPE.getCanonicalName());
		simpleTypes.add(Long.TYPE.getCanonicalName());
		simpleTypes.add(Double.TYPE.getCanonicalName());
		simpleTypes.add(Float.TYPE.getCanonicalName());
		simpleTypes.add(Boolean.TYPE.getCanonicalName());
		simpleTypes.add(Character.TYPE.getCanonicalName());
		simpleTypes.add(String.class.getCanonicalName());
		simpleTypes.add(Date.class.getCanonicalName());
		simpleTypes.add(BigInteger.class.getCanonicalName());
		simpleTypes.add(BigDecimal.class.getCanonicalName());
	}

	static boolean isSimpleType(String className)
	{
		return simpleTypes.contains(className);
	}

	/**
	 * @param rawType
	 * @return
	 */
	public static boolean hasCharacterConstructor(Class<?> rawType)
	{
		return rawType.getCanonicalName().equals(Character.class.getCanonicalName());
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static boolean hasCharacterConstructor(Type type)
	{
		Class<?> rawType = getRawType(type);
		return hasCharacterConstructor(rawType);
	}
	
	/**
	 * @param rawType
	 * @return
	 */
	public static boolean hasStringConstructor(Class<?> rawType)
	{
		return !hasCharacterConstructor(rawType);
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static boolean hasStringConstructor(Type type)
	{
		Class<?> rawType = getRawType(type);
		return hasStringConstructor(rawType);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSimpleType(Class<?> rawType)
	{
		return rawType.isEnum() || isSimpleType(rawType.getCanonicalName());
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSimpleType(Type type)
	{
		Class<?> rawType = getRawType(type);
		return isSimpleType(rawType);
	}

	public static Type getGenericReturnTypeOfGenericInterfaceMethod(Class<?> clazz, Method method)
	{
		if (!method.getDeclaringClass().isInterface())
		{
			return method.getGenericReturnType();
		}
		try
		{
			Method tmp = clazz.getMethod(method.getName(), method.getParameterTypes());
			return tmp.getGenericReturnType();
		}
		catch (NoSuchMethodException e)
		{

		}
		return method.getGenericReturnType();
	}

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
		String result = "set" + Character.toUpperCase(propertyName.charAt(0));
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
	public static String getGetterMethod(String propertyName, Class<?> baseClass)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = "" + Character.toUpperCase(propertyName.charAt(0));
		result += propertyName.substring(1);
		if (propertyName.length() > 1)
		{
			try
			{
				baseClass.getMethod("get" + result, new Class<?>[] {});
				result = "get" + result;
			}
			catch (Exception e)
			{
				try
				{
					baseClass.getMethod("is" + result, new Class<?>[] {});
					result = "is" + result;
				}
				catch (Exception e1)
				{
					result = null;
				}
			}

		}
		return result;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	public static String getGetterMethod(String propertyName)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = "" + Character.toUpperCase(propertyName.charAt(0));
		result += propertyName.substring(1);
		result = "get" + result;
		return result;
	}

	/**
	 * 
	 * @param baseType
	 * @param methodName
	 * @return
	 */
	public static boolean hasMethod(Class<?> baseType, String methodName)
	{
		return hasMethod(baseType, methodName, new Class<?>[]{});
	}

	/**
	 * 
	 * @param baseType
	 * @param methodName
	 * @param parameters
	 * @return
	 */
	public static boolean hasMethod(Class<?> baseType, String methodName, Class<?>[] parameters)
	{
		try
		{
			if (baseType.getMethod(methodName, parameters) != null)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			
		}
		return false;
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
			if (widgetType.getMethod(setterMethod, new Class<?>[] { attrType }) != null)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			try
			{
				if (attrType.isPrimitive())
				{
					Class<?> wrapperType = getBoxedClassForPrimitive(attrType);
					if (widgetType.getMethod(setterMethod, new Class<?>[] { wrapperType }) != null)
					{
						return true;
					}
				}
				else
				{
					Class<?> primitiveType = getPrimitiveFromWrapper(attrType);
					if (primitiveType != null && widgetType.getMethod(setterMethod, new Class<?>[] { primitiveType }) != null)
					{
						return true;
					}
				}
			}
			catch (Exception e1)
			{
				// Do nothing... try superclass
			}
			if (attrType.getSuperclass() != null)
			{
				return hasValidSetter(widgetType, setterMethod, attrType.getSuperclass());
			}
		}
		return false;
	}
		
	/**
	 *  workaround for JVM BUG - http://codereligion.com/post/28703017143/beware-of-java-beans-introspector
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class PropertyInfo
	{
		private final String name;
		private final Type type;
		private final Method readMethod;
		private final Method writeMethod;

		public PropertyInfo(String name, Type type, Method readMethod, Method writeMethod)
        {
			this.name = name;
			this.type = type;
			this.readMethod = readMethod;
			this.writeMethod = writeMethod;
        }

		public String getName()
        {
        	return name;
        }

		public Type getType()
        {
        	return type;
        }

		public Method getReadMethod()
        {
        	return readMethod;
        }

		public Method getWriteMethod()
        {
        	return writeMethod;
        }
	}

	public static boolean isValidSetterMethod(Method method)
	{
        return (Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("set") && method.getName().length() >3 && method.getParameterTypes().length == 1);
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isValidGetterMethod(Method method)
	{
        return Modifier.isPublic(method.getModifiers()) &&
               ((method.getName().startsWith("get") && method.getName().length() >3 
        		&& method.getParameterTypes().length == 0) && !method.getName().equals("getClass") 
        		|| (method.getName().startsWith("is") && method.getName().length() >2 
                		&& method.getParameterTypes().length == 0) 
                		&& (method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class))
        		);
	}
	
	public static List<Method> getSetterMethods(Class<?> objectType)
    {
		List<Method> result = new ArrayList<Method>();
	    Method[] methods = objectType.getMethods();
	    
	    for (Method method : methods)
        {
	        if (isValidSetterMethod(method))
	        {
	        	result.add(method);
	        }
        }
	    
	    return result;
    }

	/**
	 * 
	 * @param objectType
	 * @return
	 */
	public static List<Method> getGetterMethods(Class<?> objectType)
    {
		List<Method> result = new ArrayList<Method>();
	    Method[] methods = objectType.getMethods();
	    
	    for (Method method : methods)
        {
	        if (isValidGetterMethod(method))
	        {
	        	result.add(method);
	        }
        }
	    
	    return result;
    }
	
	public static String getPropertyForGetterOrSetterMethod(Method method)
    {
		String name = method.getName();
		if (name.startsWith("get") || name.startsWith("set"))
		{
			name = name.substring(3);
		}
		else if (name.startsWith("is"))
		{
			name = name.substring(2);
		}
		name = Character.toLowerCase(name.charAt(0))+ name.substring(1);
		
		return name;
    }
	
	/**
	 *  workaround for JVM BUG - http://codereligion.com/post/28703017143/beware-of-java-beans-introspector
	 * @param type
	 * @return
	 */
	public static PropertyInfo[] extractBeanPropertiesInfo(Type type)
	{
		Class<?> rawType = getRawType(type);
		List<PropertyInfo> result = new ArrayList<PropertyInfo>();

		List<Method> getterMethods = getGetterMethods(rawType);
		List<Method> setterMethods = getSetterMethods(rawType);
		
		try
		{
			for (Method setterMethod : setterMethods)
			{
				String setterProperty = getPropertyForGetterOrSetterMethod(setterMethod);
				for (Method getterMethod : getterMethods)
				{
					String getterProperty = getPropertyForGetterOrSetterMethod(getterMethod);
					if (getterProperty.equals(setterProperty))
					{
						Type returnType = getterMethod.getGenericReturnType();
						Type propertyType = getPropertyType(returnType, type, rawType);
						result.add(new PropertyInfo(setterProperty, propertyType, getterMethod, setterMethod));
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to determine properties for bean: " + rawType.getCanonicalName(), e);
		}
		
		return result.toArray(new PropertyInfo[result.size()]);
	}

	public static Class<?> getTypeVariableTarget(TypeVariable<?> typeVariable, Class<?> baseClass, Class<?> declaringClass)
	{
		Type baseType = getGenericDeclaringType(baseClass, declaringClass);
		if (baseType != null)
		{
			Type result = getPropertyType(typeVariable, baseType, getRawType(baseType));
			if (result instanceof Class)
			{
				return (Class<?>) result;
			}
		}
		return getRawType(typeVariable);
	}
	
	private static Type getGenericDeclaringType(Class<?> baseClass, Class<?> declaringClass)
    {
		if (baseClass.equals(declaringClass))
		{
			return baseClass;
		}
		
		if (declaringClass.isInterface())
		{
			Type[] interfaces = baseClass.getGenericInterfaces();
			for (Type type : interfaces)
            {
	            if (type instanceof ParameterizedType)
	            {
	            	if (((ParameterizedType)type).getRawType().equals(declaringClass))
	            	{
	            		return type;
	            	}
	            }
            }
		}
		else
		{
			Class<?> superclass = baseClass.getSuperclass();
			if (superclass != null && superclass.equals(declaringClass))
			{
				return baseClass.getGenericSuperclass();
			}
		}
	    return null;
    }

	private static Type getPropertyType(Type propertyType, Type baseClass, Class<?> baseRawType)
    {
		Type result = null;
		if (propertyType instanceof Class || propertyType instanceof ParameterizedType)
		{
			result = propertyType;
		}
		else if (propertyType instanceof TypeVariable)
		{
			Type[] typeArguments = ((ParameterizedType)baseClass).getActualTypeArguments();
			TypeVariable<?>[] typeParameters = baseRawType.getTypeParameters();
			String parameterName = ((TypeVariable<?>)propertyType).getName();
			
			int i=0;
			for (TypeVariable<?> typeVariable : typeParameters)
            {
	            if (parameterName.equals(typeVariable.getName()))
	            {
	            	result = typeArguments[i]; 
	            	break;
	            }
	            i++;
            }
			if (result == null)
			{
	        	throw new RuntimeException("Unable to determine property types for bean: " + baseRawType.getCanonicalName());
			}
		}
		else 
		{
        	throw new RuntimeException("Unable to determine property types for bean: " + baseRawType.getCanonicalName() + ". Type is not supported: " + propertyType);
		}
		
	    return result;
    }

	
	
	/**
	 * @param primitiveType
	 * @return
	 */
	public static Class<?> getBoxedClassForPrimitive(Class<?> primitiveType)
	{
		if (primitiveType.equals(Integer.TYPE))
		{
			return Integer.class;
		}
		else if (primitiveType.equals(Short.TYPE))
		{
			return Short.class;
		}
		else if (primitiveType.equals(Byte.TYPE))
		{
			return Byte.class;
		}
		else if (primitiveType.equals(Long.TYPE))
		{
			return Long.class;
		}
		else if (primitiveType.equals(Float.TYPE))
		{
			return Float.class;
		}
		else if (primitiveType.equals(Double.TYPE))
		{
			return Double.class;
		}
		else if (primitiveType.equals(Boolean.TYPE))
		{
			return Boolean.class;
		}
		else if (primitiveType.equals(Character.TYPE))
		{
			return Character.class;
		}
		return null;
	}

	/**
	 * @param attrType
	 * @return
	 */
	private static Class<?> getPrimitiveFromWrapper(Class<?> attrType)
	{
		if (attrType.equals(Integer.class))
		{
			return Integer.TYPE;
		}
		else if (attrType.equals(Short.class))
		{
			return Short.TYPE;
		}
		else if (attrType.equals(Long.class))
		{
			return Long.TYPE;
		}
		else if (attrType.equals(Byte.class))
		{
			return Byte.TYPE;
		}
		else if (attrType.equals(Float.class))
		{
			return Float.TYPE;
		}
		else if (attrType.equals(Double.class))
		{
			return Double.TYPE;
		}
		else if (attrType.equals(Boolean.class))
		{
			return Boolean.TYPE;
		}
		else if (attrType.equals(Character.class))
		{
			return Character.TYPE;
		}
		return null;
	}

	/**
	 * @param method
	 * @return
	 */
	public static String getMethodDescription(Method method)
	{
		StringBuilder str = new StringBuilder();

		str.append(method.getDeclaringClass().getCanonicalName());
		str.append(".");
		str.append(method.getName());
		str.append("(");
		boolean needsComma = false;

		for (Class<?> type : method.getParameterTypes())
		{
			if (needsComma)
			{
				str.append(",");
			}
			needsComma = true;
			str.append(type.getCanonicalName());
		}
		str.append(")");

		return str.toString();
	}

	/**
	 * 
	 * @param type
	 * @param field
	 * @return
	 */
	public static boolean isPropertyVisibleToWrite(Class<?> type, Field field)
	{
		return Modifier.isPublic(field.getModifiers()) || ClassUtils.hasValidSetter(type, ClassUtils.getSetterMethod(field.getName()), field.getType());
	}
	
	public static Object stringToPrimitiveBoxType(Class<?> primitiveType, String value)
	{
		if (primitiveType.equals(String.class))
		{
			return value;
		}
		if (primitiveType.equals(Boolean.TYPE))
		{
			if (value == null)
				return Boolean.FALSE;
			return Boolean.valueOf(value);
		}
		else if (value == null)
		{
			value = "0";
		}
		if (primitiveType.equals(Integer.TYPE))
		{
			return Integer.valueOf(value);
		}
		else if (primitiveType.equals(Long.TYPE))
		{
			return Long.valueOf(value);
		}
		else if (primitiveType.equals(Double.TYPE))
		{
			return Double.valueOf(value);
		}
		else if (primitiveType.equals(Float.TYPE))
		{
			return Float.valueOf(value);
		}
		else if (primitiveType.equals(Byte.TYPE))
		{
			return Byte.valueOf(value);
		}
		else if (primitiveType.equals(Short.TYPE))
		{
			return Short.valueOf(value);
		}
		else if (primitiveType.equals(Character.TYPE))
		{
			if (value != null && value.length() > 0)
			{
				return Character.valueOf(value.charAt(0));
			}
		}
		return null;
	}
	
	public static Type getCollectionBaseType(Class<?> type, Type genericType)
	{
		if (genericType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type componentGenericType = parameterizedType.getActualTypeArguments()[0];
			return componentGenericType;
		}
		else if (genericType instanceof GenericArrayType)
		{
			final GenericArrayType genericArrayType = (GenericArrayType) genericType;
			Type componentGenericType = genericArrayType.getGenericComponentType();
			return componentGenericType;
		}
		else if (type.isArray())
		{
			return type.getComponentType();
		}
		return null;
	}
	
	public static boolean isCollection(Class<?> type)
	{
		return (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
	}
	
	
	public static Class<?> getRawType(Type type)
	{
		if (type instanceof Class<?>)
		{
			// type is a normal class.
			return (Class<?>) type;
		}
		else if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			return (Class<?>) rawType;
		}
		else if (type instanceof GenericArrayType)
		{
			final GenericArrayType genericArrayType = (GenericArrayType) type;
			final Class<?> componentRawType = getRawType(genericArrayType.getGenericComponentType());
			return Array.newInstance(componentRawType, 0).getClass();
		}
		else if (type instanceof TypeVariable)
		{
			final TypeVariable<?> typeVar = (TypeVariable<?>) type;
			if (typeVar.getBounds() != null && typeVar.getBounds().length > 0)
			{
				return getRawType(typeVar.getBounds()[0]);
			}
		}
		throw new RuntimeException("Unable to determine base class from Type");
	}
	 	
	@SuppressWarnings("unchecked")
    public static <T> T findAnnotation(Annotation[] searchList, Class<T> annotation)
	{
		if (searchList == null)
		{
			return null;
		}
		for (Annotation ann : searchList)
		{
			if (ann.annotationType().equals(annotation))
			{
				return (T) ann;
			}
		}
		return null;
	}
	
	public static Type getActualValueOfTypeVariable(Class<?> clazz, TypeVariable<?> typeVariable)
	{
		if (typeVariable.getGenericDeclaration() instanceof Class<?>)
		{
			Class<?> classDeclaringTypeVariable = (Class<?>) typeVariable.getGenericDeclaration();
			// find the generic version of classDeclaringTypeVariable
			Type fromInterface = getTypeVariableViaGenericInterface(clazz, classDeclaringTypeVariable, typeVariable);
			if (fromInterface != null)
			{
				return fromInterface;
			}

			while (clazz.getSuperclass() != null)
			{
				if (clazz.getSuperclass().equals(classDeclaringTypeVariable))
				{
					// found it
					ParameterizedType parameterizedSuperclass = (ParameterizedType) clazz.getGenericSuperclass();

					for (int i = 0; i < classDeclaringTypeVariable.getTypeParameters().length; i++)
					{
						TypeVariable<?> tv = classDeclaringTypeVariable.getTypeParameters()[i];
						if (tv.equals(typeVariable))
						{
							return parameterizedSuperclass.getActualTypeArguments()[i];
						}
					}
				}

				clazz = clazz.getSuperclass();
			}
		}

		throw new RuntimeException("Unable to determine value of type parameter " + typeVariable);
	}

	private static Type getTypeVariableViaGenericInterface(Class<?> clazz, Class<?> classDeclaringTypeVariable, TypeVariable<?> typeVariable)
	{
		for (Type genericInterface : clazz.getGenericInterfaces())
		{

			if (genericInterface instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

				for (int i = 0; i < classDeclaringTypeVariable.getTypeParameters().length; i++)
				{
					TypeVariable<?> tv = classDeclaringTypeVariable.getTypeParameters()[i];
					if (tv.equals(typeVariable))
					{
						return parameterizedType.getActualTypeArguments()[i];
					}
				}
			}
			else if (genericInterface instanceof Class)
			{
				return getTypeVariableViaGenericInterface((Class<?>) genericInterface, classDeclaringTypeVariable, typeVariable);
			}
		}
		return null;
	}
}
