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
package org.cruxframework.crux.core.shared.rest;

import org.cruxframework.crux.core.shared.json.annotations.JsonIgnore;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestException extends Exception
{
    private static final long serialVersionUID = -7638825230446103361L;
    
	public RestException()
    {
	    super();
    }

	public RestException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public RestException(String message)
    {
	    super(message);
    }

	public RestException(Throwable cause)
    {
	    super(cause);
    }

	@Override
	@JsonIgnore
	public Throwable getCause()
	{
	    return super.getCause();
	}
	
	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace()
	{
	    return super.getStackTrace();
	}
	
	@Override
	@JsonIgnore
	public void setStackTrace(StackTraceElement[] stackTrace)
	{
	    super.setStackTrace(stackTrace);
	}

	@Override
	@JsonIgnore
	public String getLocalizedMessage()
	{
	    return super.getLocalizedMessage();
	}
}
