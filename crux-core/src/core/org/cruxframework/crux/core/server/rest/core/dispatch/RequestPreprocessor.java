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

import java.lang.reflect.Method;

import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class RequestPreprocessor
{
	/**
	 * 
	 * @param restOperation
	 * @return
	 */
	protected abstract boolean appliesTo(Method restOperation);
	
	/**
	 * 
	 * @param request
	 * @throws RestFailure
	 */
	public abstract void preprocess(HttpRequest request) throws RestFailure;

	/**
	 * 
	 * @param restOperation
	 * @return
	 * @throws RequestProcessorException 
	 */
	public RequestPreprocessor createProcessor(Method restOperation) throws RequestProcessorException
	{
		try
        {
	        RequestPreprocessor preprocessor = getClass().newInstance();
	        if (preprocessor.appliesTo(restOperation))
	        {
	        	return preprocessor;
	        }
        }
        catch (Exception e)
        {
        	throw new RequestProcessorException("Error creating processor", e); 
        }
        return null;
	}
}
