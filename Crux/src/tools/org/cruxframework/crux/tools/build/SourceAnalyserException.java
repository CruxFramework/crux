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
package org.cruxframework.crux.tools.build;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SourceAnalyserException extends RuntimeException
{
    private static final long serialVersionUID = 994540606474586949L;

	/**
	 * 
	 */
	public SourceAnalyserException()
	{
	}

	/**
	 * @param arg0
	 */
	public SourceAnalyserException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SourceAnalyserException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SourceAnalyserException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
}
