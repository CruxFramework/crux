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
package org.cruxframework.crux.core.server.rest.state;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * It is a very basic implementation for ResourceStateHandler interface that is
 * designed to run only on NO CUSTERED environment. It uses a simple LRUMap to
 * keep the resource state into local machine's memory.
 * 
 * To configure the cache, you can create a file named
 * NoClusteredCacheConfig.properties and configure the property
 * maxNumberOfEntries to set the max number of entries into the map.
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class NoClusteredResourceStateHandler implements ResourceStateHandler
{
	public static class LRUMap<K, V> extends LinkedHashMap<K, V>
	{
		private static final long serialVersionUID = -8939312812258005339L;
		private final int maxEntries;

		public LRUMap(int maxEntries)
		{
			super(maxEntries, 0.75f, true);
			this.maxEntries = maxEntries;
		}

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
		{
			return super.size() > maxEntries;
		}
	}

	public static class CacheEntry implements ResourceState
	{
		private final long dateModifiedMilis;
		private final long expires;
		private final String etag;

		private CacheEntry(long dateModifiedMilis, long expires, String etag)
		{
			this.dateModifiedMilis = dateModifiedMilis;
			this.expires = expires;
			this.etag = etag;
		}

		@Override
		public long getDateModified()
		{
			return dateModifiedMilis;
		}

		@Override
		public String getEtag()
		{
			return etag;
		}

		@Override
		public boolean isExpired()
		{
			return System.currentTimeMillis() - dateModifiedMilis >= expires;
		}
	}

	private Map<String, CacheEntry> cache;

	/**
	 * 
	 */
	public NoClusteredResourceStateHandler()
	{
		int maxCacheItems = Integer.parseInt(NoClusteredCacheConfigurationFactory.getConfigurations().maxNumberOfEntries());
		cache = Collections.synchronizedMap(new LRUMap<String, CacheEntry>(maxCacheItems));
	}

	@Override
	public ResourceState add(String uri, long dateModified, long expires, String etag)
	{
		CacheEntry cacheEntry = new CacheEntry(dateModified, expires, etag);
		cache.put(uri, cacheEntry);
		return cacheEntry;
	}

	@Override
	public ResourceState get(String uri)
	{
		return cache.get(uri);
	}

	@Override
	public void remove(String uri)
	{
		cache.remove(uri);
	}

	@Override
	public void clear()
	{
		cache.clear();
	}

	@Override
	public void removeSegments(String... baseURIs)
	{
		Set<String> keys = cache.keySet();

		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext())
		{
			String key = iterator.next();
			for (String baseURI : baseURIs)
			{
				if (key.startsWith(baseURI))
				{
					iterator.remove();
					break;
				}
			}
		}
	}
}