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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;

/**
 * Lightweight map implementation. This implementation has a superior performance compared
 * to HashMap. It uses native javascript implementation to increase performance.
 * 
 * @param <V> value type
 */
public class FastMap<V>
{
	private Map<V> jsMap;
	private HashMap<String, V> javaMap;

	/**
	 * Default Constructor
	 */
	public FastMap()
	{
		if (GWT.isScript())
		{
			jsMap = CollectionFactory.createMap();
		}
		else
		{
			javaMap = new HashMap<String, V>();
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public final V get(String key)
	{
		if (GWT.isScript())
		{
			return jsMap.get(key);
		}
		else
		{
			return javaMap.get(key + "");
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public final void put(String key, V value)
	{
		if (GWT.isScript())
		{
			jsMap.put(key, value);
		}
		else
		{
			javaMap.put(key, value);
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public final boolean containsKey(String key)
	{
		if (GWT.isScript())
		{
			return jsMap.containsKey(key);
		}
		else
		{
			return javaMap.containsKey(key);
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public final V remove(String key)
	{
		if (GWT.isScript())
		{
			V ret = jsMap.get(key);
			if (ret != null)
			{
				jsMap.remove(key);
			}
			return ret;
		}
		else
		{
			return javaMap.remove(key);
		}
	}
	
	/**
	 * @return
	 */
	public final FastList<String> keys()
	{
		FastList<String> ret = new FastList<String>();
		if (GWT.isScript())
		{
			jsniKeys(jsMap, ret, this);
		}
		else
		{
			for (String key : javaMap.keySet())
			{
				ret.add(key);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public final void clear()
	{
		if (GWT.isScript())
		{
			jsMap.clear();
		}
		else
		{
			javaMap.clear();
		}
	}
	
	/**
	 * 
	 */
	public final boolean isEmpty()
	{
		if (GWT.isScript())
		{
			return jsMap.isEmpty();
		}
		else
		{
			return javaMap.size() == 0;
		}
	}

	
	/**
	 * @param jsMap
	 * @param keys
	 * @param map
	 */
	private final native void jsniKeys(Map<V> jsMap, FastList<String> keys, FastMap<V> map) /*-{
	    for (var k in jsMap) {
        	map.@org.cruxframework.crux.core.client.collection.FastMap::addKey(Lorg/cruxframework/crux/core/client/collection/FastList;Ljava/lang/String;)(keys, k);
	    }
    }-*/;
	
	/**
	 * @param keys
	 * @param value
	 */
	@SuppressWarnings("unused")
	private final void addKey(FastList<String> keys, String value)
	{
		keys.add(value);
	}
}
