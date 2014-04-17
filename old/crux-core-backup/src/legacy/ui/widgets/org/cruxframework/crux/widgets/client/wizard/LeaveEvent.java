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
package org.cruxframework.crux.widgets.client.wizard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.Legacy;



/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Legacy
@Deprecated
public class LeaveEvent<T extends Serializable> extends StepEvent<LeaveHandler<T>, T> 
{
	private static Map<Class<?>, Type<LeaveHandler<?>>> TYPES = new HashMap<Class<?>, Type<LeaveHandler<?>>>();

	private T resource;
	private boolean canceled;
	private final String nextStep;

	/**
	 * 
	 */
	protected LeaveEvent (WizardProxy<T> wizardProxy, String nextStep)
	{
		super(wizardProxy);
		this.nextStep = nextStep;
		if (wizardProxy != null)
		{
			this.resource = wizardProxy.getResource();
		}
	}

	/**
	 * @return
	 */
	public static Type<LeaveHandler<?>> getType(Class<?> clazz)
	{
		if (!TYPES.containsKey(clazz)) 
		{
			TYPES.put(clazz, new Type<LeaveHandler<?>>());
		}

		return TYPES.get(clazz);
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static <T extends Serializable> LeaveEvent<T> fire(HasLeaveHandlers<T> source, WizardProxy<T> proxy, String nextStep)
	{
		LeaveEvent<T> event = new LeaveEvent<T>(proxy, nextStep);
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(LeaveHandler<T> handler)
	{
		handler.onLeave(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Type<LeaveHandler<T>> getAssociatedType()
	{
		return (Type) getType(this.resource.getClass());
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * 
	 */
	public void cancel()
	{
		canceled = true;
	}

	public String getNextStep()
    {
    	return nextStep;
    }
}
