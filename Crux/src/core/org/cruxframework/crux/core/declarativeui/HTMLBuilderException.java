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
package org.cruxframework.crux.core.declarativeui;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLBuilderException extends Exception
{

    private static final long serialVersionUID = 6075364357371138553L;

	/**
	 * 
	 */
	public HTMLBuilderException()
	{
	}

	/**
	 * @param message
	 */
	public HTMLBuilderException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public HTMLBuilderException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HTMLBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
