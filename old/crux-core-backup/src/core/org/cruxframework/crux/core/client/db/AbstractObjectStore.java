/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.db;


/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * <p>Base class for object stores on Crux Database. Use the interface ObjectStore to access your stores.</p> 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractObjectStore<K, V> extends DBObject implements ObjectStore<K, V> 
{
	protected AbstractObjectStore(AbstractDatabase db)
	{
		super(db);
	}
	
	@Override
	public void put(V object) 
	{
		put(object, null);
	}

	@Override
	public void add(V object) 
	{
		add(object, null);
	}
	
	public void delete(K key) 
	{
		delete(key, null);
	}
	
	@Override
	public void delete(KeyRange<K> keyRange)
	{
		delete(keyRange, null);
	}
}
