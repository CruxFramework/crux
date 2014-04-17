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
package org.cruxframework.crux.core.server.launcher;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxLaunchException extends RuntimeException
{
    private static final long serialVersionUID = 5114273528616502598L;

	public CruxLaunchException()
    {
        super();
    }

	public CruxLaunchException(String message, Throwable cause)
    {
        super(message, cause);
    }

	public CruxLaunchException(String message)
    {
        super(message);
    }

	public CruxLaunchException(Throwable cause)
    {
        super(cause);
    }
}

