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

import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;

/**
 * Generate an eTag based in the content, using MD5.
 * 
 * @author @author Thiago da Rosa de Bustamante
 */
public class MD5ETagHandlerImpl implements ETagHandler
{
	private static final Log logger = LogFactory.getLog(MD5ETagHandlerImpl.class);

	@Override
    public String generateEtag(UriInfo uri, String content)
    {
		if (StringUtils.isEmpty(content))
		{
			return null;
		}
		
		try
        {
			byte[] bytes = content.getBytes("UTF-8");
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        byte[] hashBytes = digest.digest(bytes);
	        BigInteger bigInt = new BigInteger(1, hashBytes);
	        String hashtext = bigInt.toString(16);
			return hashtext;
        }
        catch (Exception e)
        {
        	logger.error("Error generating etag with MD5 algorithm. Ignoring Etag for this resource ["+uri.getPath()+"]...", e);
        }
		return null;
    }
}