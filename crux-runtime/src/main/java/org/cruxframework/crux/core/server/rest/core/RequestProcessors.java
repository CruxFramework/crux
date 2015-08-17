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
package org.cruxframework.crux.core.server.rest.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.cruxframework.crux.core.server.rest.core.dispatch.RequestPostprocessor;
import org.cruxframework.crux.core.server.rest.core.dispatch.RequestPreprocessor;
import org.cruxframework.crux.core.server.rest.core.dispatch.RequestProcessorException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RequestProcessors
{
	private static Map<String, RequestPreprocessor> preProcessors = Collections.synchronizedMap(new LinkedHashMap<String, RequestPreprocessor>());
	private static Map<String, RequestPostprocessor> postProcessors = Collections.synchronizedMap(new LinkedHashMap<String, RequestPostprocessor>());	
	
	public static void registerPreprocessor(String processorClassName)
	{
        try
        {
        	RequestPreprocessor preprocessor = (RequestPreprocessor) Class.forName(processorClassName).newInstance();
	        preProcessors.put(processorClassName, preprocessor);
        }
        catch (Exception e)
        {
        	throw new RequestProcessorException("Error registering preprocessor ["+processorClassName+"]", e);
        }
	}
	
	public static void registerPostprocessor(String processorClassName)
	{
        try
        {
        	RequestPostprocessor postprocessor = (RequestPostprocessor) Class.forName(processorClassName).newInstance();
	        postProcessors.put(processorClassName, postprocessor);
        }
        catch (Exception e)
        {
        	throw new RequestProcessorException("Error registering postprocessor ["+processorClassName+"]", e);
        }
	}
	
	public static Iterator<RequestPreprocessor> iteratePreprocessors()
	{
		return preProcessors.values().iterator();
	}

	public static Iterator<RequestPostprocessor> iteratePostprocessors()
	{
		return postProcessors.values().iterator();
	}
}
