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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.core.registry.ResourceRegistry;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.HttpResponse;
import org.cruxframework.crux.core.server.rest.spi.NotFoundException;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class RestDispatcher
{
	private static final Log logger = LogFactory.getLog(RestDispatcher.class);

	public static MethodReturn dispatch(HttpRequest request, HttpResponse response, boolean preflightRequest) throws RestFailure
	{
		ResourceMethod invoker = getInvoker(request, !preflightRequest);
		MethodReturn methodReturn = null;
		if (invoker.checkCorsPermissions(request, response, preflightRequest) && !preflightRequest)
		{
			methodReturn = invoker.invoke(request, response);
		}
		return methodReturn;
	}

	private static ResourceMethod getInvoker(HttpRequest request, boolean ensureExists) throws RestFailure
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("PathInfo: " + request.getUri().getPath());
		}
		ResourceMethod invoker = ResourceRegistry.getInstance().getResourceMethod(request);
		if (ensureExists && invoker == null)
		{
			throw new NotFoundException("Unable to find resource associated with path: " + request.getUri().getPath());
		}
		return invoker;
	}
}
