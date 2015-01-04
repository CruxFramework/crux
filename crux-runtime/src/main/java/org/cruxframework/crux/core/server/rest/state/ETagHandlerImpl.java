/*
 * Copyright 2014 cruxframework.org.
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

import org.apache.commons.lang.StringUtils;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;

/**
 * Generate a default eTag based in the content.
 * 
 * @author @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public class ETagHandlerImpl implements ETagHandler
{
	@Override
    public String generateEtag(UriInfo uri, String content)
    {
		if (StringUtils.isEmpty(content))
		{
			return null;
		} 
		return Long.toHexString(System.currentTimeMillis()) + Integer.toHexString(content.hashCode());
    }
}