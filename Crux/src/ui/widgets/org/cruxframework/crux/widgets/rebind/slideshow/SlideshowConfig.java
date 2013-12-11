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
package org.cruxframework.crux.widgets.rebind.slideshow;

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
import org.cruxframework.crux.widgets.client.slideshow.Slideshow.Layout;
import org.cruxframework.crux.widgets.client.slideshow.Slideshow.Name;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService;


/**
 * Maps all layoutNames in a module.
 * @author Thiago Bustamante
 *
 */
public class SlideshowConfig 
{
	private static final Log logger = LogFactory.getLog(SlideshowConfig.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> layoutNames;
	private static Map<String, String> serviceNames;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (layoutNames != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (layoutNames != null)
			{
				return;
			}
			
			initializeLayouts();
			initializeServices();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeLayouts()
	{
		layoutNames = new HashMap<String, String>();
		
		Set<String> layoutClassNames =  ClassScanner.searchClassesByInterface(Layout.class);
		if (layoutClassNames != null)
		{
			for (String layout : layoutClassNames) 
			{
				try 
				{
					Class<?> layoutClass = Class.forName(layout);
					Name annot = layoutClass.getAnnotation(Name.class);
					if (annot != null)
					{
						if (layoutNames.containsKey(annot.value()))
						{
							throw new CruxGeneratorException("Duplicated Slideshow Layout: ["+annot.value()+"].");
						}

						layoutNames.put(annot.value(), layoutClass.getCanonicalName());
					}
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing slideshow layout.",e);
				}
			}
		}
	}

	protected static void initializeServices()
	{
		serviceNames = new HashMap<String, String>();
		
		Set<String> serviceClassNames =  ClassScanner.searchClassesByInterface(AlbumService.class);
		if (serviceClassNames != null)
		{
			for (String service : serviceClassNames) 
			{
				try 
				{
					Class<?> serviceClass = Class.forName(service);
					Name annot = serviceClass.getAnnotation(Name.class);
					if (annot != null)
					{
						if (serviceNames.containsKey(annot.value()))
						{
							throw new CruxGeneratorException("Duplicated Slideshow Service: ["+annot.value()+"].");
						}

						serviceNames.put(annot.value(), serviceClass.getCanonicalName());
					}
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing slideshow Service.",e);
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static String getLayout(String name)
	{
		if (layoutNames == null)
		{
			initialize();
		}
		return layoutNames.get(name);
	}

	
	/**
	 * @return
	 */
	public static Iterator<String> iterateLayouts()
	{
		if (layoutNames == null)
		{
			initialize();
		}
		return layoutNames.keySet().iterator();
	}

	/**
	 * @param name
	 * @return
	 */
	public static String getService(String name)
	{
		if (serviceNames == null)
		{
			initialize();
		}
		return serviceNames.get(name);
	}

	
	/**
	 * @return
	 */
	public static Iterator<String> iterateServices()
	{
		if (serviceNames == null)
		{
			initialize();
		}
		return serviceNames.keySet().iterator();
	}
}
