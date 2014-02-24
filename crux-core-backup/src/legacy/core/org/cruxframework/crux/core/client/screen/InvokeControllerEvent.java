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
package org.cruxframework.crux.core.client.screen;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.event.CruxEvent;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public class InvokeControllerEvent extends CruxEvent<Screen>
{
	private Object parameter;
	
	/**
	 * @param source
	 * @param senderId
	 */
	InvokeControllerEvent()
	{
		super(Screen.get(), Screen.getId());
	}

	/**
	 * 
	 * @return
	 */
	public Object getParameter()
	{
		return parameter;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param parameterClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParameter(Class<T> parameterClass)
	{
		return (T)getParameter();
	}

	/**
	 * 
	 * @return
	 */
	public Object getParameter(int index)
	{
		if (parameter == null)
		{
			return null;
		}
		else if (parameter instanceof Object[])
		{
			return ((Object[])parameter)[index];
		}
		else if (index == 0)
		{
			return parameter;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * 
	 * @param <T>
	 * @param index
	 * @param parameterClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParameter(int index, Class<T> parameterClass)
	{
		return (T)getParameter(index);
	}

	/**
	 * 
	 * @return
	 */
	public int getParameterCount()
	{
		if (parameter == null)
		{
			return 0;
		}
		else if (parameter instanceof Object[])
		{
			return ((Object[])parameter).length;
		}
		else
		{
			return 1;
		}
	}
	
	void setParameter(Object data)
	{
		this.parameter = data;
	}
}
