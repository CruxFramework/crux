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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.annotation.RestService.CorsSupport;
import org.cruxframework.crux.core.utils.StreamUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CorsData
{
	private static final Log logger = LogFactory.getLog(CorsData.class);

	private Set<String> allowedOrigin = new HashSet<String>();
	private Set<String> exposeHeaders = new HashSet<String>();
	private Set<String> allowMethods = new HashSet<String>();
	private boolean allowCredentials = false;
	private boolean allowAllOrigins = false;
	private long maxAge;
	
	void addAllowedOrigin(String origin)
	{
		allowedOrigin.add(origin);
	}
	
	public Iterator<String> getAllowMethods()
    {
        return allowMethods.iterator();
    }

	void addAllowMethod(String method)
	{
		allowMethods.add(method);
	}

	public boolean isAllowMethod(String method)
	{
		return allowMethods.contains(method);
	}

	void addExposeHeaders(String header)
	{
		exposeHeaders.add(header);
	}
	
	void setAllowCredentials(boolean allowCredentials)
	{
		this.allowCredentials = allowCredentials;
	}
	
	void setAllowAllOrigins(boolean allowAllOrigins)
	{
		this.allowAllOrigins = allowAllOrigins;
	}
	
	void setMaxAge(long maxAge)
	{
		this.maxAge = maxAge;
	}

	public long getMaxAge()
	{
		return this.maxAge;
	}
	
	public boolean isAllOriginsAllowed()
	{
		return allowAllOrigins;
	}
	
	public boolean isOriginAllowed(String origin)
	{
		return allowAllOrigins || allowedOrigin.contains(origin);
	}
	
	public Iterator<String> getExposeHeaders()
	{
		return exposeHeaders.iterator();
	}
	
	public boolean isAllowCredentials()
	{
		return allowCredentials;
	}
	
	static CorsData parseCorsData(CorsSupport corsSupport)
	{
		CorsData data = null;
		if (corsSupport != null)
		{
			data = new CorsData();
			Set<String> allowOrigins = getAllowOrigins(corsSupport);
			data.setAllowAllOrigins(allowOrigins.contains("*"));
			if (!data.isAllOriginsAllowed())
			{
				for (String origin : allowOrigins)
				{
					data.addAllowedOrigin(origin);
				}
			}
			data.setAllowCredentials(corsSupport.allowCredentials());
			data.setMaxAge(corsSupport.maxAge());
			for (String header : corsSupport.exposeHeaders())
			{
				data.addExposeHeaders(header);
			}
		}
		return data;
	}
	
	static Set<String> getAllowOrigins(CorsSupport corsSupport)
	{
		Set<String> result = new HashSet<String>();
		
		for (String origin : corsSupport.allowOrigin())
        {
	        result.add(origin);
        }
		
		String configFile = corsSupport.allowOriginConfigFile();
		
		if (configFile != null && configFile.length() > 0)
		{
			InputStream stream = CorsData.class.getClassLoader().getResourceAsStream(configFile);
			if (stream != null)
			{
				try
                {
	                String content = StreamUtils.readAsUTF8(stream);
	                String[] lines = content.split("\n\r");
	        		for (String origin : lines)
	                {
	        	        result.add(origin.trim());
	                }
                }
                catch (IOException e)
                {
	                logger.error("Error reading allowOrigin config file ["+configFile+"]", e);;
                }
			}
		}
		return result;
	}
}