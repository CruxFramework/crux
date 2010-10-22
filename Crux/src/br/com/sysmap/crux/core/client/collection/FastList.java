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
package br.com.sysmap.crux.core.client.collection;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

/**
 * Lightweight List implementation.
 * 
 * @param <V>
 *            value type
 */
public class FastList<V>
{
	private Array<V> jsArray;
	private ArrayList<V> javaArray;

	/**
	 * 
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
	 * @param index
	 * @return
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
	 * @param value
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
	 * @param value
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
	 * @param key
	 * @return
	 */
	public final boolean contains(V value)
	{
		if (GWT.isScript())
		{
			for(int i=0; i<jsArray.size(); i++)
			{
				if (jsArray.get(i) != null)
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return javaArray.contains(value);
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public final V remove(int index)
	{
		if (GWT.isScript())
		{
			V ret = jsArray.get(index);
			if (ret != null)
			{
				jsArray.remove(index);
			}
			return ret;
		}
		else
		{
			return javaArray.remove(index);
		}
	}
	
	/**
	 * @return
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
	 * @param value
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
