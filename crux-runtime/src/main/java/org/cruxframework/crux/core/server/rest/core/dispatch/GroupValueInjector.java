/*
 * Copyright 2013 cruxframework.org.
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.server.rest.core.dispatch.MethodInvoker.RestParameterType;
import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.ClassUtils.PropertyInfo;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class GroupValueInjector implements ValueInjector
{
	protected ValueInjector[] params;
	private PropertyInfo[] properties;
	private final Class<?> baseClass;
	
	public GroupValueInjector(RestParameterType restParameterType, Type type, String paramPrefix)
    {
		this.baseClass = ClassUtils.getRawType(type);
		if (!isAllowedComplexType(baseClass))
		{
			throw new InternalServerErrorException("Invalid rest parameter for rest method: " + baseClass.getCanonicalName() + ". Type not allowed for " +
					"this type of parameter. It can only be passed as a body parameter", "Can not execute requested service");
		}
		List<PropertyInfo> writeableProperties = new ArrayList<PropertyInfo>();
		PropertyInfo[] properties = ClassUtils.extractBeanPropertiesInfo(type);

		List<ValueInjector> injectors = new ArrayList<ValueInjector>();
	    if (properties != null)
	    {
	    	for (PropertyInfo property : properties)
            {
	            if (property.getWriteMethod() != null)
	            {
	            	writeableProperties.add(property);
					Type propertyType = property.getType();
					if (ClassUtils.isSimpleType(propertyType))
	            	{
	            		switch (restParameterType)
	            		{
		            		case query: 
		            			injectors.add(new QueryParamInjector(propertyType, getParamName(paramPrefix, property.getName()), null));
	            			break;
		            		case header:
		            			injectors.add(new HeaderParamInjector(propertyType, getParamName(paramPrefix, property.getName()), null));
	            			break;
		            		case form:
		            			injectors.add(new FormParamInjector(propertyType, getParamName(paramPrefix, property.getName()), null));
	            			break;
		            		case cookie:
		            			injectors.add(new CookieParamInjector(propertyType, getParamName(paramPrefix, property.getName()), null));
	            			break;
		            		case path:
		            			injectors.add(new PathParamInjector(propertyType, getParamName(paramPrefix, property.getName()), null));
	            			break;
		            		default:
							break;
	            		}
	            	}
	            	else
	            	{
	            		injectors.add(new GroupValueInjector(restParameterType, propertyType, getParamName(paramPrefix, property.getName())));
	            	}
	            }
            }
	    }
	    params = injectors.toArray(new ValueInjector[injectors.size()]);
	    this.properties = writeableProperties.toArray(new PropertyInfo[writeableProperties.size()]);
    }

	private boolean isAllowedComplexType(Class<?> type)
	{
		if (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type))
		{
			return false;
		}
		
		return true;
	}
	
	private String getParamName(String paramPrefix, String name)
    {
		StringBuilder builder = new StringBuilder();
		
		if (paramPrefix!= null && paramPrefix.trim().length() > 0)
		{
			builder.append(paramPrefix);
			
			if (!paramPrefix.endsWith("."))
			{
				builder.append(".");
			}
		}
		
		builder.append(name);
		
	    return builder.toString();
    }

	@Override
    public Object inject(HttpRequest request)
    {
		Object result;
        try
        {
	        result = baseClass.newInstance();
	        for (int i=0; i< params.length; i++)
	        {
	        	properties[i].getWriteMethod().invoke(result, params[i].inject(request));
	        }
        }
        catch (Exception e)
        {
			throw new BadRequestException("Can not read request values for ValueObject parameter: " + baseClass.getCanonicalName(), 
										  "Can not read request parameter for path: " + request.getUri().getPath(), e);
        }
		return result;
    }
}