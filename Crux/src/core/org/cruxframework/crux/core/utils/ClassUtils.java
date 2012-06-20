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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

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
	public static String getGetterMethod(String propertyName, Class<?> baseClass)
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
	            baseClass.getMethod("get"+result, new Class<?>[]{});
                result = "get"+result;
            }
            catch (Exception e)
            {
	            try
                {
	                baseClass.getMethod("is"+result, new Class<?>[]{});
	                result = "is"+result;
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
		String result = ""+Character.toUpperCase(propertyName.charAt(0)); 
		result += propertyName.substring(1);
        result = "get"+result;
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
			if (widgetType.getMethod(setterMethod, new Class<?>[]{attrType}) != null)
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
					if (widgetType.getMethod(setterMethod, new Class<?>[]{wrapperType}) != null)
					{
						return true;
					}
				}
				else
				{
					Class<?> primitiveType = getPrimitiveFromWrapper(attrType);
					if (primitiveType != null && widgetType.getMethod(setterMethod, new Class<?>[]{primitiveType}) != null)
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
	 * 
	 * @param processorClass
	 * @return
	 */
	public static TagConstraints getChildTagConstraintsAnnotation(Class<?> processorClass)
	{
		TagConstraints attributes = processorClass.getAnnotation(TagConstraints.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && !superClass.equals(WidgetCreator.class))
			{
				attributes = getChildTagConstraintsAnnotation(superClass);
			}
		}
		
		return attributes;
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
		
		for ( Class<?> type : method.getParameterTypes())
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
		return Modifier.isPublic(field.getModifiers()) || 
				ClassUtils.hasValidSetter(type, ClassUtils.getSetterMethod(field.getName()), field.getType());
    }
}
