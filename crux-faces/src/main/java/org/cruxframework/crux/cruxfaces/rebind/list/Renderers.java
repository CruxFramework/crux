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
package org.cruxframework.crux.cruxfaces.rebind.list;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.cruxfaces.client.list.ScrollableList.Renderer;



/**
 * Maps all custom cells.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Renderers 
{
	private static final Log logger = LogFactory.getLog(Renderers.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> renderers;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (renderers != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (renderers != null)
			{
				return;
			}
			
			initializerenderers();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializerenderers()
	{
		renderers = new HashMap<String, String>();
		
		Set<String> cellNames =  ClassScanner.searchClassesByAnnotation(Renderer.class);
		if (cellNames != null)
		{
			for (String cellClass : cellNames) 
			{
				try 
				{
					Class<?> dataClass = Class.forName(cellClass);
					Renderer annot = dataClass.getAnnotation(Renderer.class);
					if (renderers.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated Renderer found: ["+annot.value()+"].");
					}
					
					renderers.put(annot.value(), dataClass.getCanonicalName());
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing Renderers.",e);
				}
			}
		}
	}
	

	/**
	 * @param name
	 * @return
	 */
	public static String getRenderer(String name)
	{
		if (renderers == null)
		{
			initialize();
		}
		return renderers.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateRenderers()
	{
		if (renderers == null)
		{
			initialize();
		}
		return renderers.keySet().iterator();
	}
}
