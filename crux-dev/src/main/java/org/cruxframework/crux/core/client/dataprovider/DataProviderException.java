/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.dataprovider;

/**
 * Exception to describe a {@link DataProvider} operation error.
 * @author Thiago da Rosa de Bustamante
 */
public class DataProviderException extends RuntimeException
{
    private static final long serialVersionUID = -579649646989882710L;

	public DataProviderException()
    {
	    super();
    }

	public DataProviderException(String message, Throwable t)
    {
	    super(message, t);
    }

	public DataProviderException(String message)
    {
	    super(message);
    }

	public DataProviderException(Throwable t)
    {
	    super(t);
    }
}
