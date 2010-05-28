/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.formatter;

/**
 * 
 * @author Thiago
 */
public class InvalidFormatException extends RuntimeException 
{
	private static final long serialVersionUID = -8583878317698972266L;

	public InvalidFormatException() 
	{
	}

	public InvalidFormatException(String message) 
	{
		super(message);
	}

	public InvalidFormatException(Throwable cause) 
	{
		super(cause);
	}

	public InvalidFormatException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
