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
package org.cruxframework.crux.gadget.client.screen;

import org.cruxframework.crux.core.client.screen.URLRewriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.io.IoProvider;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetURLRewriter extends URLRewriter
{
	private int cachePeriod = 0;
	private GadgetConstants constants = GWT.create(GadgetConstants.class);
	
	/**
	 * 
	 */
	public GadgetURLRewriter()
    {
	    cachePeriod = constants.cachePeriod();
    }
	
	@Override
	public String rewrite(String url)
	{
	    if (url == null)
	    {
	    	return null;
	    }
		String urlRewrite = super.rewrite(url);
	    if (!urlRewrite.toLowerCase().startsWith("http:") && !urlRewrite.toLowerCase().startsWith("https:"))
	    {
	    	String urlGadget = Window.Location.getParameter("url");
	    	if (urlGadget == null)
	    	{
	    		urlGadget = Window.Location.getHost()+"/"+Window.Location.getPath();
	    	}
	    	else
	    	{
	    		int index = urlGadget.indexOf('?');
	    		if (index > 0)
	    		{
	    			urlGadget = urlGadget.substring(0, index);
	    		}
	    		index = urlGadget.lastIndexOf('/');
	    		if (index > 0)
	    		{
	    			urlGadget = urlGadget.substring(0, index);
	    		}
	    	}
	    	urlRewrite = urlGadget+"/"+urlRewrite;
	    }
	    
	    if (isCacheable(urlRewrite))
	    {
		    return IoProvider.get().getProxyUrl(urlRewrite, cachePeriod);
	    }
	    
	    return IoProvider.get().getProxyUrl(urlRewrite);
	}
	
	/**
	 * 
	 * @param urlRewrite
	 * @return
	 */
	private boolean isCacheable(String urlRewrite)
    {
	    return urlRewrite.contains(".cache.");
    }

	/**
	 * 
	 * @return
	 */
	public static String getGadgetUrl()
	{
    	String urlGadget = Window.Location.getParameter("url");
    	if (urlGadget == null)
    	{
    		urlGadget = Window.Location.getHost()+"/"+Window.Location.getPath();
    	}
    	else
    	{
    		int index = urlGadget.indexOf('?');
    		if (index > 0)
    		{
    			urlGadget = urlGadget.substring(0, index);
    		}
    	}
    	return urlGadget;
	}
}
