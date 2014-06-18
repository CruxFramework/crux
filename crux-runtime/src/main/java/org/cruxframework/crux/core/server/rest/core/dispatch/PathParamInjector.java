/*
 * Copyright 2011 cruxframework.org
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

import java.lang.reflect.Type;
import java.util.List;

import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class PathParamInjector extends StringParameterInjector implements ValueInjector
{

	public PathParamInjector(Type type, String paramName, String defaultValue)
	{
		super(ClassUtils.getRawType(type), paramName, defaultValue);
	}

	public Object inject(HttpRequest request)
	{
		List<String> list = request.getUri().getPathParameters(true).get(paramName);
		if (list == null)
		{
			throw new InternalServerErrorException("Unknown @PathParam: " + paramName + " for path: " + request.getUri().getPath(), "Can not execute requested service with informed params.");
		}
		if (list != null && list.size() > 0)
		{
			return extractValue(list.get(list.size() - 1));
		}
		return null;
	}
}