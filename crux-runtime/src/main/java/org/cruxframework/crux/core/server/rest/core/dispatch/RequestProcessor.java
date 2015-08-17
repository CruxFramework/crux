/*
 * Copyright 2015 cruxframework.org.
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

/**
 * @author thiago
 *
 */
public abstract class RequestProcessor
{
	/**
	 * To be overriden.
	 * @param context
	 * @return
	 */
	protected boolean appliesTo(RequestProcessorContext context)
	{
		return true;
	}
	
	/**
	 * To be overriden.
	 * @param context
	 * @return
	 */
	protected boolean appliesTo(Method targetMethod)
	{
		return true;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws RequestProcessorException 
	 */
	@SuppressWarnings("unchecked")
    public <T extends RequestProcessor> T createProcessor(RequestProcessorContext context) throws RequestProcessorException
	{
		try
        {
	        RequestProcessor preprocessor = getClass().newInstance();
	        
	        if(preprocessor.appliesTo(context) && preprocessor.appliesTo(context.getTargetMethod()))
	        {
	        	return (T) preprocessor;
	        }
        }
        catch (Exception e)
        {
        	throw new RequestProcessorException("Error creating processor", e); 
        }
		
        return null;
	}
	
}