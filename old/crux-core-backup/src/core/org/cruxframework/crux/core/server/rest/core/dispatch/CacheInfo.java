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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.GET.CacheControl;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CacheInfo
{
	private int cacheTime;
	private boolean noTransform;
	private boolean mustRevalidate;
	private boolean proxyRevalidate;
	private CacheControl cacheControl;

	private CacheInfo(){}
	
	public int getCacheTime()
    {
    	return cacheTime;
    }

	public void setCacheTime(int cacheTime)
    {
    	this.cacheTime = cacheTime;
    }

	public CacheControl getCacheControl()
    {
    	return cacheControl;
    }

	public void setCacheControl(CacheControl cacheControl)
    {
    	this.cacheControl = cacheControl;
    }

	public long defineExpires()
	{
		return defineExpires(System.currentTimeMillis());
	}

	public long defineExpires(long current)
	{
		long expires = current + (cacheTime*1000);
		return expires;
	}
	
	public boolean isCacheEnabled()
	{
		return cacheTime > 0;
	}
	
	
	public boolean isNoTransform()
    {
    	return noTransform;
    }

	public void setNoTransform(boolean noTransform)
    {
    	this.noTransform = noTransform;
    }

	public boolean isMustRevalidate()
    {
    	return mustRevalidate;
    }

	public void setMustRevalidate(boolean mustRevalidate)
    {
    	this.mustRevalidate = mustRevalidate;
    }

	public boolean isProxyRevalidate()
    {
    	return proxyRevalidate;
    }

	public void setProxyRevalidate(boolean proxyRevalidate)
    {
    	this.proxyRevalidate = proxyRevalidate;
    }

	public static CacheInfo parseCacheInfo(GET get)
    {
		CacheInfo cacheInfo = new CacheInfo();
		cacheInfo.setCacheTime(get.cacheTime());
		cacheInfo.setCacheControl(get.cacheControl());
		cacheInfo.setNoTransform(get.noTransform());
		cacheInfo.setMustRevalidate(get.mustRevalidate());
		cacheInfo.setProxyRevalidate(get.proxyRevalidate());
	    return cacheInfo;
    }
}
