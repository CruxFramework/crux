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
package org.cruxframework.crux.core.client.controller.crossdoc;

/**
 * This exception is used to report errors during a cross document call.
 * @author Thiago da Rosa de Bustamante
 * @see CrossDocument
 */
public class CrossDocumentException extends RuntimeException
{
    private static final long serialVersionUID = 3225034035194466410L;

	/**
	 * 
	 */
	public CrossDocumentException()
	{
	}

	/**
	 * @param message
	 */
	public CrossDocumentException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CrossDocumentException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CrossDocumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
