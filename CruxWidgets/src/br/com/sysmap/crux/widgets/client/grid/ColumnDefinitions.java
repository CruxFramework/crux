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

import java.util.Iterator;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

/**
 * Represents the columns to be rendered by a grid widget.
 * @author Gesse S. F. Dafe
 */
public class ColumnDefinitions
{
	private FastList<ColumnDefinition> definitionsInOrder = new FastList<ColumnDefinition>();
	private FastMap<ColumnDefinition> definitionsByKey = new FastMap<ColumnDefinition>();
	private FastMap<Integer> actualColumnIndexes = new FastMap<Integer>();
	private int visibleColumnCount = -1;
	
	void setGrid(Grid grid)
	{
		for (int i=0; i<definitionsInOrder.size(); i++)
		{
			ColumnDefinition columnDefinition = definitionsInOrder.get(i);
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
	FastList<ColumnDefinition> getDefinitions()
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
				
				for (int j=0; j<definitionsInOrder.size(); j++)
				{
					ColumnDefinition column = definitionsInOrder.get(j);
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
			
			for (int i=0; i<definitionsInOrder.size(); i++)
			{
				ColumnDefinition def = definitionsInOrder.get(i);
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
		private FastList<T> defs;
	
		/**
		 * Restrict constructor 
		 * @param definitions
		 */
		ColumnIterator(FastList<T> definitions)
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