/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

/**
 * Represents the columns to be rendered by a grid widget.
 * @author Gesse S. F. Dafe
 */
public class ColumnDefinitions
{
	private List<ColumnDefinition> definitionsInOrder = new ArrayList<ColumnDefinition>();
	private Map<String, ColumnDefinition> definitionsByKey = new HashMap<String, ColumnDefinition>();
	private Map<String, Integer> actualColumnIndexes = new HashMap<String, Integer>();
	private int visibleColumnCount = -1;
	
	void setGrid(Grid grid)
	{
		for (ColumnDefinition columnDefinition : definitionsInOrder)
		{
			columnDefinition.setGrid(grid);
		}
	}
	
	/**
	 * Register a new column definition
	 * @param key
	 * @param definition
	 */
	public void add(String key, ColumnDefinition definition)
	{
		definitionsInOrder.add(definition);
		definitionsByKey.put(key, definition);
		definition.setKey(key);
	}
	
	/**
	 * Gets all registered columns definition
	 */
	List<ColumnDefinition> getDefinitions()
	{
		return definitionsInOrder;
	}
	
	/**
	 * Gets a registered column definition by its key
	 * @param key
	 */
	ColumnDefinition getDefinition(String key)
	{
		return definitionsByKey.get(key);
	}
	
	/**
	 * Gets the column definition index 
	 * @param key
	 * @param considerInvisibleColumns 
	 */
	int getColumnIndex(String key, boolean considerInvisibleColumns)
	{
		if(!considerInvisibleColumns)
		{
			Integer index = actualColumnIndexes.get(key);
			if(index == null)
			{
				int i = -1;
				
				for (ColumnDefinition column : definitionsInOrder)
				{
					if(column.isVisible())
					{
						i++;
					}
					
					if(column.getKey().equals(key))
					{
						index = i;
						actualColumnIndexes.put(key, index);
					}
				}
			}
			return index;
		}
		else
		{
			return definitionsInOrder.indexOf(definitionsByKey.get(key));
		}	
	}
	
	/**
	 * 
	 */
	void reset()
	{
		this.visibleColumnCount = -1;
		this.actualColumnIndexes.clear();
	}
	
	/**
	 * Gets the number of columns that will be rendered
	 * @return the visible column count
	 */
	int getVisibleColumnCount()
	{
		if(this.visibleColumnCount == -1)
		{
			this.visibleColumnCount = 0;
			
			for (ColumnDefinition def : definitionsInOrder)
			{
				if(def.isVisible())
				{
					this.visibleColumnCount++;
				}
			}
		}
		
		return this.visibleColumnCount ;		
	}
	
	/**
	 * Creates and returns a iterator for accessing column definitions in an ordered way
	 */
	public Iterator<ColumnDefinition> getIterator()
	{
		return new ColumnIterator<ColumnDefinition>(definitionsInOrder);
	}
		
	/**
	 * Iterator for the registered column definition
	 * @author Gesse S. F. Dafe
	 */
	public static class ColumnIterator<T extends ColumnDefinition> implements Iterator<T>
	{
		int cursor = 0;
		private List<T> defs;
	
		/**
		 * Restrict constructor 
		 * @param definitions
		 */
		ColumnIterator(List<T> definitions)
		{
			this.defs = definitions;
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return cursor < defs.size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public T next()
		{
			if(hasNext())
			{
				return (defs.get(cursor++));
			}
			else
			{
				return null;
			}
		}

		/**
		 * Unsupported method. It's not possible unregister a columns definition.
		 * @throws UnsupportedOperationException 
		 */
		public void remove()
		{
			throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().removingColumnDefinitionByIterator());
		}		
	}
}