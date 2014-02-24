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
package org.cruxframework.crux.core.server.rest.spi;

import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;

/**
 * Thrown when HTTP Internal Service Error (500) is encountered
 */
public class InternalServerErrorException extends RestFailure
{
	private static final long serialVersionUID = -2432300008647858571L;

	public InternalServerErrorException(String s)
	{
		super(s, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
	}

	public InternalServerErrorException(String s, Throwable throwable)
	{
		super(s, throwable, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
	}

	public InternalServerErrorException(String s, String resp, Throwable throwable)
	{
		super(s, resp, throwable, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
	}

	public InternalServerErrorException(String s, String resp)
	{
		super(s, resp, null, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
	}

	public InternalServerErrorException(Throwable throwable)
	{
		super(throwable, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
	}
}