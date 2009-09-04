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

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
	
	static Class<?> getReflectionEquivalentTypeForPrimities(Class<?> attrType)
	{
		if (attrType.equals(Integer.class))
		{
			return Integer.TYPE;
		}
		else if (attrType.equals(Integer.TYPE))
		{
			return Integer.class;
		}
		else if (attrType.equals(Short.class))
		{
			return Short.TYPE;
		}
		else if (attrType.equals(Short.TYPE))
		{
			return Short.class;
		}
		else if (attrType.equals(Long.class))
		{
			return Long.TYPE;
		}
		else if (attrType.equals(Long.TYPE))
		{
			return Long.class;
		}
		else if (attrType.equals(Byte.class))
		{
			return Byte.TYPE;
		}
		else if (attrType.equals(Byte.TYPE))
		{
			return Byte.class;
		}
		else if (attrType.equals(Float.class))
		{
			return Float.TYPE;
		}
		else if (attrType.equals(Float.TYPE))
		{
			return Float.class;
		}
		else if (attrType.equals(Double.class))
		{
			return Double.TYPE;
		}
		else if (attrType.equals(Double.TYPE))
		{
			return Double.class;
		}
		else if (attrType.equals(Boolean.class))
		{
			return Boolean.TYPE;
		}
		else if (attrType.equals(Boolean.TYPE))
		{
			return Boolean.class;
		}
		else if (attrType.equals(Character.class))
		{
			return Character.TYPE;
		}
		else if (attrType.equals(Character.TYPE))
		{
			return Character.class;
		}
		else
		{
			return null;
		}
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
	 * @param classType
	 * @return
	 */
	public static String getClassBinaryName(JClassType classType)
	{
		String pkgName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName();
		String name = classType.getName();
		
		if (name.equals(simpleName))
		{
			return pkgName + "." +name;
		}
		else
		{
			return pkgName + "." + name.substring(0, name.indexOf(simpleName)-1) + "$"+ simpleName;
		}
	}	
	
}
