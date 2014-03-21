/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.converter;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.converter.TypeConverter.Converter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;

/**
 * Maps all converters.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Converters 
{
	private static final Log logger = LogFactory.getLog(Converters.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> converters;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (converters != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (converters != null)
			{
				return;
			}
			
			initializeConverters();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeConverters()
	{
		converters = new HashMap<String, String>();
		
		Set<String> converterNames =  ClassScanner.searchClassesByAnnotation(Converter.class);
		if (converterNames != null)
		{
			for (String converterClassName : converterNames) 
			{
				try 
				{
					Class<?> converterClass = Class.forName(converterClassName);
					Converter annot = converterClass.getAnnotation(Converter.class);
					if (converters.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated Converter found: ["+annot.value()+"].");
					}
					
					converters.put(annot.value(), converterClass.getCanonicalName());
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing Converters ["+converterClassName+"].",e);
				}
			}
		}
	}
	

	/**
	 * @param name
	 * @return
	 */
	public static String getConverter(String name)
	{
		if (converters == null)
		{
			initialize();
		}
		return converters.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateConverters()
	{
		if (converters == null)
		{
			initialize();
		}
		return converters.keySet().iterator();
	}
}
