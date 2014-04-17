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

import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestErrorHandlerImpl extends RestErrorHandler
{	
	@Override
    public Object handleError(InvocationTargetException e) throws RestFailure
    {
		Throwable cause = e.getCause();
		if (isCheckedException(cause))
		{
			cause.setStackTrace(new StackTraceElement[]{});
			return cause;
		}
		else
		{
			throw new InternalServerErrorException("Can not execute requested service. Unchecked Exception occurred on method: "+method.toString(), 
					"Can not execute requested service", cause);
		}
    }
}
