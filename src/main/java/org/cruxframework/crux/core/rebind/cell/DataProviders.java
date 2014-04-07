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
package org.cruxframework.crux.core.rebind.cell;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.cell.CustomCell;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.scanner.ClassScanner;


/**
 * Maps all custom cells.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataProviders 
{
	private static final Log logger = LogFactory.getLog(DataProviders.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> customCells;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (customCells != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (customCells != null)
			{
				return;
			}
			
			initializeCustomCells();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeCustomCells()
	{
		customCells = new HashMap<String, String>();
		
		Set<String> cellNames =  ClassScanner.searchClassesByAnnotation(CustomCell.class);
		if (cellNames != null)
		{
			for (String cellClass : cellNames) 
			{
				try 
				{
					Class<?> dataClass = Class.forName(cellClass);
					CustomCell annot = dataClass.getAnnotation(CustomCell.class);
					if (customCells.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated CustomCell found: ["+annot.value()+"].");
					}
					
					customCells.put(annot.value(), dataClass.getCanonicalName());
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing CustomCells.",e);
				}
			}
		}
	}
	

	/**
	 * @param name
	 * @return
	 */
	public static String getCustomCell(String name)
	{
		if (customCells == null)
		{
			initialize();
		}
		return customCells.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateCustomCells()
	{
		if (customCells == null)
		{
			initialize();
		}
		return customCells.keySet().iterator();
	}
}
