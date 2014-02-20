/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.tools.parameters;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConsoleParametersProcessingException extends RuntimeException
{
	private static final long serialVersionUID = -6979570093132712436L;

	public ConsoleParametersProcessingException()
	{
		super();
	}

	public ConsoleParametersProcessingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ConsoleParametersProcessingException(String message)
	{
		super(message);
	}

	public ConsoleParametersProcessingException(Throwable cause)
	{
		super(cause);
	}
}
