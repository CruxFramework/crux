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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.blocks.Cache;
import org.jgroups.blocks.ReplCache;

/**
 * It is a very basic implementation of ResourceStateHandler interface for clustered environments.
 * This implementation is based on JGroups ReplCache component and can be used as basis for most 
 * complete implementations on top of more powerful cache systems, like Infinispan, EhCache, OsCache, 
 * JCS or any other Cache system you prefer. If you choose to use this implementation, you must 
 * include jgroups.jar on your classpath.
 * 
 * To configure the cache, you can create a file named ClusteredCacheConfig.properties and configure 
 * the following properties:
 * 
 * channelConfigPropertyFile - JGroups channel config file name
 * rpcTimeout - Timeout for replCache rpc calls
 * useL1Cache - To enable or disable L1 Cache
 * l1ReapingInterval - If L1 cache is enabled, the interval to run the Expired Values Cleaner Thread for L1 Cache
 * l1MaxNumberOfEntries - If L1 cache is enabled, the max number of entries for L1 Cache
 * l2ReapingInterval - The interval to run the Expired Values Cleaner Thread for L2 Cache
 * l1MaxNumberOfEntries - The max number of entries for L2 Cache
 * clusterName - The name of the cluster to be used by this cache
 * replCount - The number of nodes in cluster where the information will be replicated 
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ClusteredResourceStateHandler implements ResourceStateHandler
{
	private static final Log logger = LogFactory.getLog(ClusteredResourceStateHandler.class);
	private ReplCache<String, CacheEntry> cache;
	private short replCount;

	public static class CacheEntry implements ResourceState, Serializable
	{
		private static final long serialVersionUID = -7144309067971959838L;
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

	/**
	 * 
	 */
	public ClusteredResourceStateHandler()
	{
		try
		{
			ClusteredCacheConfig config = ClusteredCacheConfigurationFactory.getConfigurations();

			replCount = Short.parseShort(config.replCount());
			cache = new ReplCache<String, CacheEntry>(config.channelConfigPropertyFile(), config.clusterName());
			cache.setMigrateData(true);
			cache.setCallTimeout(Integer.parseInt(config.rpcTimeout()));
			cache.setCachingTime(Integer.parseInt(config.cachingTime()));
			cache.setDefaultReplicationCount(replCount);
			if (Boolean.parseBoolean(config.useL1Cache()))
			{
				Cache<String,CacheEntry> l1Cache=new Cache<String,CacheEntry>();
				cache.setL1Cache(l1Cache);
				int l1ReapingInterval = Integer.parseInt(config.l1ReapingInterval());
				if (l1ReapingInterval > 0)
				{
					l1Cache.enableReaping(l1ReapingInterval);
				}
				int l1MaxNumberOfEntries = Integer.parseInt(config.l1MaxNumberOfEntries());
				if (l1MaxNumberOfEntries > 0)
				{
					l1Cache.setMaxNumberOfEntries(l1MaxNumberOfEntries);
				}
			}
			Cache<String,ReplCache.Value<CacheEntry>> l2Cache=cache.getL2Cache();
			int l2ReapingInterval = Integer.parseInt(config.l2ReapingInterval());
			if (l2ReapingInterval > 0)
			{
				l2Cache.enableReaping(l2ReapingInterval);
			}
			int l2MaxNumberOfEntries = Integer.parseInt(config.l2MaxNumberOfEntries());
			if (l2MaxNumberOfEntries > 0)
			{
				l2Cache.setMaxNumberOfEntries(l2MaxNumberOfEntries);
			}

			cache.start();
		}
		catch (Exception e)
		{
			logger.error("Error connecting to resources distributed cache", e);
			e.printStackTrace();
		}
	}

	@Override
	public ResourceState add(String uri, long dateModified, long expires, String etag)
	{
		CacheEntry cacheEntry = new CacheEntry(dateModified, expires, etag);
		cache.put(uri, cacheEntry, replCount, expires);//(key, val, repl_count, timeout, synchronous)
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
	public void removeSegments(String... baseURIs)
	{
		Set<String> keys = cache.getL2Cache().getInternalMap().keySet();
		Set<String> keysToRemove = new HashSet<String>();
		for (String key : keys)
		{
			for (String baseURI: baseURIs)
			{
				if (key.startsWith(baseURI))
				{
					keysToRemove.add(key);
					break;
				}
			}
		}
		for (String key : keysToRemove)
		{
			cache.remove(key, true);
		}	        
	}
	
	@Override
	public void clear()
	{
		cache.clear();
	}
}