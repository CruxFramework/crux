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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.shared.rest.RestException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class RestErrorHandler
{
	private Class<?>[] exceptionTypes;
	protected Method method;

	protected void setMethod(Method method)
    {
		this.method = method;
		this.exceptionTypes = getRestExceptionTypes(method);
    }
	
	/**
	 * When an error occurs during a REST service invocation, a RestErrorHandler is called to handle this error.
	 * This method must take the exception and throw a RestFailure to wrap the error or return an object that 
	 * will be serialized as response given from the service to the caller. 
	 * @param error
	 * @param method
	 * @return
	 * @throws RestFailure
	 */
	public abstract Object handleError(InvocationTargetException error) throws RestFailure;
	
	protected Class<?>[] getRestExceptionTypes(Method method)
    {
		List<Class<?>> result = new ArrayList<Class<?>>();
	    Class<?>[] types = method.getExceptionTypes();
	    for (Class<?> exceptionClass : types)
        {
            if (RestException.class.isAssignableFrom(exceptionClass))
            {
            	result.add(exceptionClass);
            }
        }
		return result.toArray(new Class[result.size()]);
    }

	protected boolean isCheckedException(Throwable throwable)
	{
		if (exceptionTypes != null)
		{
			for (Class<?> exception : exceptionTypes)
            {
	            if (exception.isAssignableFrom(throwable.getClass()))
	            {
	            	return true;
	            }
            }
		}
		return false;
	}
}
