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

import org.cruxframework.crux.core.server.rest.core.dispatch.RequestPreprocessor;
import org.cruxframework.crux.core.server.rest.core.dispatch.RequestProcessorException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RequestPreprocessors
{
	private static Map<String, RequestPreprocessor> processors = Collections.synchronizedMap(new LinkedHashMap<String, RequestPreprocessor>());
	
	public static void registerPreprocessor(String processorClassName)
	{
        try
        {
        	RequestPreprocessor preprocessor = (RequestPreprocessor) Class.forName(processorClassName).newInstance();
	        processors.put(processorClassName, preprocessor);
        }
        catch (Exception e)
        {
        	throw new RequestProcessorException("Error creating preprocessor ["+processorClassName+"]", e);
        }
	}
	
	public static Iterator<RequestPreprocessor> iteratePreprocessors()
	{
		return processors.values().iterator();
	}
}
