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
package org.cruxframework.crux.core.client.screen;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * Override this class if you need to specify some url re-writing solution, like when working 
 * inside a Gadget container.
 * @author Thiago da Rosa de Bustamante
 */
public class URLRewriter
{
	public String rewrite(String url)
    {
		if (Crux.getConfig().enableDebugForURL(url))
		{
			if (!StringUtils.isEmpty(url) && !url.contains("gwt.codesvr=") && !url.startsWith("data:"))
			{
				String debugSvr = Window.Location.getParameter("gwt.codesvr");
				if (!StringUtils.isEmpty(debugSvr))
				{
					if (url.contains("?"))
					{
						url += "&gwt.codesvr="+URL.encode(debugSvr); 
					}
					else
					{
						url += "?gwt.codesvr="+URL.encode(debugSvr); 
					}
				}
			}
		}
		return url;
    }
}
