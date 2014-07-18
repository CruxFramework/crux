/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class StringParameterInjector
{
	private static final String CAN_NOT_INVOKE_USING_ARGUMENTS_MSG = "Can not invoke requested service with given arguments";
	protected Class<?> rawType;
	protected Constructor<?> constructor;
	protected Method valueOf;
	protected String defaultValue;
	protected String paramName;
	private boolean isDate;
	private boolean isSQLDate;

	protected StringParameterInjector()
    {
    }
	
	protected StringParameterInjector(Class<?> type, String paramName, String defaultValue)
	{
		initialize(type, paramName, defaultValue);
	}

	protected void initialize(Class<?> type, String paramName, String defaultValue)
	{
		this.rawType = type;
		this.paramName = paramName;
		this.defaultValue = defaultValue;
		this.isDate = Date.class.isAssignableFrom(type);
		this.isSQLDate = java.sql.Date.class.isAssignableFrom(type);

		if (ClassUtils.isSimpleType(rawType))
		{
			try
			{
				if(ClassUtils.hasStringConstructor(type))
				{
					constructor = type.getConstructor(String.class);
				} else if(ClassUtils.hasCharacterConstructor(rawType))
				{
					constructor = type.getConstructor(Character.TYPE);	
				}
				
				if (constructor != null && !Modifier.isPublic(constructor.getModifiers()))
				{
					constructor = null;
				}
			}
			catch (NoSuchMethodException ignored)
			{
				//No error, try valueOf method
			}
		}
		if (constructor == null)
		{
			valueOf = findValueOfMethod();
		}
	}

	protected Method findValueOfMethod()
    {
	    Method fromString = null;
	    Method valueOf = null;
	    try
	    {
	    	fromString = rawType.getDeclaredMethod("fromString", String.class);
	    	if (Modifier.isStatic(fromString.getModifiers()) == false)
	    	{
	    		fromString = null;
	    	}
	    }
	    catch (NoSuchMethodException ignored)
	    {
			//No error, continue searching
	    }
	    try
	    {
	    	valueOf = rawType.getDeclaredMethod("valueOf", String.class);
	    	if (Modifier.isStatic(valueOf.getModifiers()) == false)
	    	{
	    		valueOf = null;
	    	}
	    }
	    catch (NoSuchMethodException ignored)
	    {
			//No error, continue searching
	    }
	    if (valueOf == null)
	    {
	    	valueOf = fromString;
	    }
	    return valueOf;
    }

	public String getParamSignature()
	{
		return rawType.getName() + "(\"" + paramName + "\")";
	}

	public Object extractValue(String strVal)
	{
		if (strVal == null || strVal.length()==0)
		{
			if (defaultValue == null)
			{
				if (!rawType.isPrimitive())
				{
					return null;
				}
			}
			else
			{
				strVal = defaultValue;
			}
		}
		if (isSQLDate && isNumeric(strVal))
		{
			return new java.sql.Date(Long.parseLong(strVal));
		} else if (isDate && isNumeric(strVal))  
		{
			return new Date(Long.parseLong(strVal));
		}  else if (rawType.isPrimitive())
		{
			return ClassUtils.stringToPrimitiveBoxType(rawType, strVal);
		}
		else if (constructor != null)
		{
			try
			{
				return constructor.newInstance(strVal);
			}
			catch (Exception e)
			{
				String errorMsg = "Unable to extract parameter from http request for " + getParamSignature();
				
				if(constructor.getName().equals(Character.class.getCanonicalName()))
				{
					try
					{
						return constructor.newInstance(strVal.toCharArray()[0]);
					} catch (Exception f)
					{
						throw new BadRequestException(errorMsg, CAN_NOT_INVOKE_USING_ARGUMENTS_MSG, e);		
					}
				}
				
				throw new BadRequestException(errorMsg, CAN_NOT_INVOKE_USING_ARGUMENTS_MSG, e);
			}
		}
		else if (valueOf != null)
		{
			try
			{
				return valueOf.invoke(null, strVal);
			}
			catch (Exception e)
			{
				throw new BadRequestException("Unable to extract parameter from http request: " + getParamSignature(), CAN_NOT_INVOKE_USING_ARGUMENTS_MSG, e);
			}
		}
		return null;
	}

	private boolean isNumeric(String str)
    {
		if (str == null)
		{
			return false;
		}
		int sz = str.length();
		int startPos = 0;
		if (sz > 0 && str.charAt(0) == '-')
		{
			startPos = 1;
		}
		for (int i = startPos; i < sz; i++)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
    }
}