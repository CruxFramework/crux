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
package org.cruxframework.crux.core.rebind.dto;
 
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.dto.DataObjectIdentifier;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.ClassUtils;


/**
 * Maps all data objects.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataObjects 
{
	private static final Log logger = LogFactory.getLog(DataObjects.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> dataObjects;
	private static Map<String, String[]> dataObjectIdentifiers;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (dataObjects != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (dataObjects != null)
			{
				return;
			}
			
			initializeDataObjects();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeDataObjects()
	{
		dataObjects = new HashMap<String, String>();
		dataObjectIdentifiers = new HashMap<String, String[]>();
		
		initializeDefaultDataObjects();
		
		Set<String> dataNames =  ClassScanner.searchClassesByAnnotation(DataObject.class);
		if (dataNames != null)
		{
			for (String dataObject : dataNames) 
			{
				try 
				{
					Class<?> dataClass = Class.forName(dataObject);
					DataObject annot = dataClass.getAnnotation(DataObject.class);
					if (dataObjects.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated DataObject found: ["+annot.value()+"].");
					}
					
					dataObjects.put(annot.value(), dataClass.getCanonicalName());
					dataObjectIdentifiers.put(annot.value(), extractIdentifiers(dataClass));
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing DataObjects.",e);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private static void initializeDefaultDataObjects()
    {
		dataObjects.put(String.class.getSimpleName(), String.class.getCanonicalName());
		dataObjects.put(Integer.class.getSimpleName(), Integer.class.getCanonicalName());
		dataObjects.put(Short.class.getSimpleName(), Short.class.getCanonicalName());
		dataObjects.put(Byte.class.getSimpleName(), Byte.class.getCanonicalName());
		dataObjects.put(Long.class.getSimpleName(), Long.class.getCanonicalName());
		dataObjects.put(Float.class.getSimpleName(), Float.class.getCanonicalName());
		dataObjects.put(Double.class.getSimpleName(), Double.class.getCanonicalName());
		dataObjects.put(Boolean.class.getSimpleName(), Boolean.class.getCanonicalName());
		dataObjects.put(Date.class.getSimpleName(), Date.class.getCanonicalName());
		dataObjects.put(Character.class.getSimpleName(), Character.class.getCanonicalName());
		dataObjects.put("int","int");
		dataObjects.put("long","long");
		dataObjects.put("byte","byte");
		dataObjects.put("short","short");
		dataObjects.put("float","float");
		dataObjects.put("double","double");
		dataObjects.put("boolean","boolean");
		dataObjects.put("char","char");
    }

	/**
	 * @param dataClass
	 * @return
	 */
	private static String[] extractIdentifiers(Class<?> dataClass)
    {
		Class<?> dtoClass = dataClass;
		
		List<String> ids = new ArrayList<String>();
		while(dtoClass.getSuperclass() != null)
		{
			Field[] fields = dtoClass.getDeclaredFields();

			for (Field field : fields)
			{
				if (field.getAnnotation(DataObjectIdentifier.class) != null)
				{
					if (Modifier.isPublic(field.getModifiers()))
					{
						ids.add(field.getName());
					}
					else
					{
						ids.add(ClassUtils.getGetterMethod(field.getName(), dtoClass)+"()");
					}
				}
			}
			dtoClass = dtoClass.getSuperclass();
		}
		return ids.toArray(new String[ids.size()]);
    }

	/**
	 * @param name
	 * @return
	 */
	public static String getDataObject(String name)
	{
		if (dataObjects == null)
		{
			initialize();
		}
		return dataObjects.get(name);
	}
	
	public static String[] getDataObjectIdentifiers(String name)
	{
		if (dataObjectIdentifiers == null)
		{
			initialize();
		}
		return dataObjectIdentifiers.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateDataObjects()
	{
		if (dataObjects == null)
		{
			initialize();
		}
		return dataObjects.keySet().iterator();
	}
}
