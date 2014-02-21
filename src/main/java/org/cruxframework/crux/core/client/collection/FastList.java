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
package org.cruxframework.crux.core.client.collection;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

/**
 * Lightweight List implementation. This implementation has a superior performance compared
 * to ArrayList. It uses native javascript implementation on production to increase performance.
 * 
 * @param <V> value type
 * @author Thiago da Rosa de Bustamante
 */
public class FastList<V>
{
	private Array<V> jsArray;
	private ArrayList<V> javaArray;

	/**
	 * Constructor 
	 */
	public FastList()
	{
		if (GWT.isScript())
		{
			jsArray = CollectionFactory.createArray();
		}
		else
		{
			javaArray = new ArrayList<V>();
		}
	}

	/**
	 * Retrieve an element from the list, based on the specified index 
	 * @param index element position
	 * @return element from list
	 */
	public final V get(int index)
	{
		if (GWT.isScript())
		{
			return jsArray.get(index);
		}
		else
		{
			return javaArray.get(index);
		}
	}

	/**
	 * Retrieve the first element and remove it from the list 
	 * @return first element
	 */
	public final V extractFirst()
	{
		if (size() > 0)
		{
			if (GWT.isScript())
			{
				V ret = jsArray.get(0);
				jsArray.remove(0);
				return ret;
			}
			else
			{
				return javaArray.remove(0);
			}
		}
		return null;
	}
	
	/**
	 * Insert the given element at the end of the list.
	 * @param value element to add
	 */
	public final void add(V value)
	{
		if (GWT.isScript())
		{
			jsArray.add(value);
		}
		else
		{
			javaArray.add(value);
		}
	}

	/**
	 * Insert the given element right before the specified position.
	 * @param beforeIndex position to put the element
	 * @param value element to add
	 */
	public final void add(int beforeIndex, V value)
	{
		if (GWT.isScript())
		{
			jsArray.insert(beforeIndex, value);
		}
		else
		{
			javaArray.add(beforeIndex, value);
		}
	}
	
	/**
	 * Check if the the list contains the specified value
	 * @param value value to search 
	 * @return true if the element is present inside the list
	 */
	public final boolean contains(V value)
	{
		if (GWT.isScript())
		{
			return jsArray.indexOf(value) != -1;
		}
		else
		{
			return javaArray.contains(value);
		}
	}
	
	/**
	 * Remove the element at the given position.
	 * @param index element position
	 * @return the removed element
	 */
	public final V remove(int index)
	{
		if (GWT.isScript())
		{
			V ret = jsArray.get(index);
			jsArray.remove(index);
			return ret;
		}
		else
		{
			return javaArray.remove(index);
		}
	}
	
	/**
	 * Return the list size
	 * @return number of elements on this list
	 */
	public final int size()
	{
		if (GWT.isScript())
		{
			return jsArray.size();
		}
		else
		{
			return javaArray.size();
		}		
	}
	
	/**
	 * Remove all elements on this list
	 */
	public final void clear()
	{
		if (GWT.isScript())
		{
			jsArray.clear();
		}
		else
		{
			javaArray.clear();
		}
	}	

	/**
	 * Retrieve the position of the given element on this list.
	 * @param value element to search
	 * @return element position. If not present, return -1;
	 */
	public final int indexOf(V value)
	{
		if (GWT.isScript())
		{
			return jsArray.indexOf(value);
		}
		else
		{
			return javaArray.indexOf(value);
		}
	}

	/**
	 * Set the given element into the specified position on this list.
	 * @param index element position
	 * @param value element to set
	 */
	public final void set(int index, V value)
	{
		if (GWT.isScript())
		{
			jsArray.set(index, value);
		}
		else
		{
			javaArray.set(index, value);
		}
	}
}
