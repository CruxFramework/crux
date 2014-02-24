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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.lang.reflect.Method;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetCreatorHelper 
{
	private final Class<?> factoryClass;
	private final Class<?> widgetType;
	private final Class<?> contextType;

	/**
	 * @param factoryClass
	 */
	public WidgetCreatorHelper(Class<?> factoryClass)
    {
		this.factoryClass = factoryClass;
		this.widgetType = getWidgetTypeFromClass();
		this.contextType = getContextTypeFromClass();
    }

	/**
	 * @return
	 */
	public Class<?> getFactoryClass()
    {
    	return factoryClass;
    }

	public String getWidgetDeclarationType()
	{
		DeclarativeFactory declarativeFactory = factoryClass.getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.library()+"_"+declarativeFactory.id();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return
	 */
	public Class<?> getWidgetType()
    {
    	return widgetType;
    }
	
	/**
	 * @return
	 */
	public Class<?> getContextType()
    {
    	return contextType;
    }

	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	public TagConstraints getChildtrenAttributesAnnotation(Class<?> processorClass)
	{
		TagConstraints attributes = processorClass.getAnnotation(TagConstraints.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && superClass.getSuperclass() != null)
			{
				attributes = getChildtrenAttributesAnnotation(superClass);
			}
		}
		
		return attributes;
	}	
	
	/**
	 * 
	 * @return
	 */
	private Class<?> getWidgetTypeFromClass()
	{
		DeclarativeFactory declarativeFactory = factoryClass.getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.targetWidget();
		}
		else
		{
			return null;
		}
	}	

	/**
	 * 
	 * @return
	 */
	private Class<?> getContextTypeFromClass()
	{
        try
        {
	        Method method = factoryClass.getMethod("instantiateContext", new Class<?>[]{});
	        return method.getReturnType();
        }
        catch (Exception e)
        {
			throw new CruxGeneratorException("Error generating widget factory ["+factoryClass.getName()+"]. Can not realize the type for generic declaration.");
        }
	}	
}
