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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.cell.CustomCell;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.JClassScanner;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;


/**
 * Maps all custom cells.
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public class CustomCellScanner 
{
	private Map<String, String> customCells;
	private boolean initialized = false;
	private JClassScanner jClassScanner;

	public CustomCellScanner(GeneratorContext context)
	{
		jClassScanner = new JClassScanner(context);
	}

	/**
	 * @param name
	 * @return
	 */
	public String getCustomCell(String name)
	{
		initializeCustomCells();
		return customCells.get(name);
	}


	/**
	 * @return
	 */
	public Iterator<String> iterateCustomCells()
	{
		initializeCustomCells();
		return customCells.keySet().iterator();
	}

	/**
	 * 
	 */
	protected void initializeCustomCells()
	{
		if (!initialized)
		{
			customCells = new HashMap<String, String>();
			JClassType[] cellTypes;
			try 
			{
				cellTypes =  jClassScanner.searchClassesByAnnotation(CustomCell.class);
			} 
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error initializing CustomCells.",e);
			}
			if (cellTypes != null)
			{
				for (JClassType cellClass : cellTypes) 
				{
					CustomCell annot = cellClass.getAnnotation(CustomCell.class);
					if (customCells.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated CustomCell found: ["+annot.value()+"].");
					}
					
					customCells.put(annot.value(), cellClass.getQualifiedSourceName());
				}
			}
			initialized = true;
		}
	}
}
