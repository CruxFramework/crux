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
package org.cruxframework.crux.core.server.rest.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectMapper.DefaultTypeResolverBuilder;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;
import org.codehaus.jackson.type.JavaType;
import org.cruxframework.crux.core.shared.json.annotations.JsonIgnore;
import org.cruxframework.crux.core.shared.json.annotations.JsonSubTypes;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.ClassUtils.PropertyInfo;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JsonUtil
{
	private static ObjectMapper defaultMapper;
	private static ObjectMapper subTypeAwareMapper;
	
	private static final Lock lock = new ReentrantLock();

	public static ObjectReader createReader(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		setGlobalConfigurations(mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectReader reader = mapper.reader(paramJavaType);
		return reader;
	}

	public static ObjectWriter createWriter(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		setGlobalConfigurations(mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectWriter writer = mapper.writerWithType(paramJavaType);
		return writer;
	}
	
	private static void setGlobalConfigurations(ObjectMapper mapper) 
	{
		mapper.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
	}

	private static ObjectMapper getObjectMapper(Type type)
    {
		if(defaultMapper == null)
		{
			lock.lock();
			try
			{
				if(defaultMapper == null)
				{
					defaultMapper = new ObjectMapper();
					defaultMapper.setSerializerFactory(new CruxSerializerFactory());
					subTypeAwareMapper = new ObjectMapper();
					TypeResolverBuilder<?> builder = new SubTypeResolverBuilder();
					builder = builder.init(JsonTypeInfo.Id.CLASS, null);
					builder = builder.inclusion(JsonTypeInfo.As.PROPERTY);
					builder = builder.typeProperty(JsonSubTypes.SUB_TYPE_SELECTOR);			
					subTypeAwareMapper.setDefaultTyping(builder);
					subTypeAwareMapper.setSerializerFactory(new CruxSerializerFactory());
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		Class<?> clazz = ClassUtils.getRawType(type);
		return getObjectMapper(type, clazz);
    }

	private static ObjectMapper getObjectMapper(Type type, Class<?> clazz)
    {
		if (clazz != null && hasJsonSubTypes(type, clazz, new HashSet<Class<?>>())) 
		{
			return subTypeAwareMapper;
		}
		return defaultMapper;
    }

	private static boolean hasJsonSubTypes(Type type, Class<?> clazz, Set<Class<?>> searched)
    {
		while (ClassUtils.isCollection(clazz))
		{
			type = ClassUtils.getCollectionBaseType(clazz, type);
			clazz = ClassUtils.getRawType(type);
		}
		if (!searched.contains(clazz))
		{
			searched.add(clazz);
			JsonSubTypes jsonSubTypes = clazz.getAnnotation(JsonSubTypes.class);
			if (jsonSubTypes != null && jsonSubTypes.value() != null)
			{
				if (jsonSubTypes.value().length > 0)
				{
					return true;
				}
			}
			
			PropertyInfo[] propertiesInfo = ClassUtils.extractBeanPropertiesInfo(type);
			if (propertiesInfo != null)
			{
				for (PropertyInfo propertyInfo : propertiesInfo)
                {
	                if (hasJsonSubTypes(propertyInfo.getType(), ClassUtils.getRawType(propertyInfo.getType()), searched))
	                {
	                	return true;
	                }
                }
			}
			
		}
		return false;
    }
	
	private static class SubTypeResolverBuilder extends DefaultTypeResolverBuilder
	{
		public SubTypeResolverBuilder()
        {
	        super(DefaultTyping.NON_FINAL);
        }
		
		@Override
		public boolean useForType(JavaType t)
		{
			JsonSubTypes jsonSubTypes = t.getRawClass().getAnnotation(JsonSubTypes.class);
			if (jsonSubTypes != null && jsonSubTypes.value() != null)
			{
				return (jsonSubTypes.value().length > 0);
			}
			
		    return false;
		}
	}
	
	private static class CruxSerializerFactory extends BeanSerializerFactory
	{
		protected CruxSerializerFactory()
        {
	        super(new BeanSerializerFactory.ConfigImpl());
        }

		@Override
		protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig serializationConfig, 
																BasicBeanDescription beanDescription, 
																List<BeanPropertyWriter> props)
		{
			//filter out standard properties (e.g. those marked with @JsonIgnore)
	        props = super.filterBeanProperties(serializationConfig, beanDescription, props);

	        Class<?> beanClass = beanDescription.getBeanClass();
			//filter out standard properties (e.g. those marked with @org.cruxframework.crux.core.shared.json.annotations.JsonIgnore)
	        for (Iterator<BeanPropertyWriter> iter = props.iterator(); iter.hasNext();) 
	        {
	        	BeanPropertyWriter beanPropertyWriter = iter.next();
	        	String getterMethodName = ClassUtils.getGetterMethod(beanPropertyWriter.getName(), beanClass);
	        	try
                {
	                Method getterMethod = beanClass.getMethod(getterMethodName);
		        	if (getterMethod != null && getterMethod.isAnnotationPresent(JsonIgnore.class)) 
		            {
		                iter.remove();
		            }
                }
	        	catch (Exception e)
	        	{
	        		//ignore property
	        	}
	        }

	        return props;
	    }		
	}
}