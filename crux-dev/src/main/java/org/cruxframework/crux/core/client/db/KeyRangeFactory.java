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
 * A factory for KeyRange objects
 * @param <K> The type of the key used by KeyRanges produced by this factory .
 * @author Thiago da Rosa de Bustamante
 */
public interface KeyRangeFactory<K>
{
	/**
	 * Create a KeyRange including only the given Key
	 * @param key
	 * @return
	 */
	KeyRange<K> only(K key);
	
	/**
	 * Create a KeyRange including all keys greater than the given key
	 * @param key
	 * @param open if true, does not include the lower bound
	 * @return
	 */
	KeyRange<K> lowerBound(K key, boolean open);

	/**
	 * Create a KeyRange including all keys greater than the given key
	 * @param key
	 * @return
	 */
	KeyRange<K> lowerBound(K key);

	/**
	 * Create a KeyRange including all keys smaller than the given key
	 * @param key
	 * @param open if true, does not include the upper bound
	 * @return
	 */
	KeyRange<K> upperBound(K key, boolean open);

	/**
	 * Create a KeyRange including all keys smaller than the given key
	 * @param key
	 * @return
	 */
	KeyRange<K> upperBound(K key);
	
	/**
	 * Create a KeyRange including all keys between upper and lower bound keys
	 * @param startKey
	 * @param endKey
	 * @param startOpen if true, does not include the lower bound
	 * @param endOpen if true, does not include the upper bound
	 * @return
	 */
	KeyRange<K> bound(K startKey, K endKey, boolean startOpen, boolean endOpen);

	/**
	 * Create a KeyRange including all keys between upper and lower bound keys
	 * @param startKey
	 * @param endKey
	 * @return
	 */
	KeyRange<K> bound(K startKey, K endKey);
}
