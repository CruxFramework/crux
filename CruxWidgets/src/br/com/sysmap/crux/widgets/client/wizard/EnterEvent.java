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
package br.com.sysmap.crux.widgets.client.wizard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class EnterEvent<T extends Serializable> extends StepEvent<EnterHandler<T>, T> 
{
	private static Map<Class<?>, Type<EnterHandler<?>>> TYPES = new HashMap<Class<?>, Type<EnterHandler<?>>>();

	private T resource;
	private final String previousStep;
	
	/**
	 * 
	 */
	protected EnterEvent(WizardProxy<T> wizardProxy, String previousStep)
	{
		super(wizardProxy);
		this.previousStep = previousStep;
		if (wizardProxy != null)
		{
			this.resource = wizardProxy.getResource();
		}
	}

	/**
	 * @return
	 */
	public static Type<EnterHandler<?>> getType(Class<?> clazz)
	{
		if (!TYPES.containsKey(clazz)) 
		{
			TYPES.put(clazz, new Type<EnterHandler<?>>());
		}

		return TYPES.get(clazz);
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static <T extends Serializable> EnterEvent<T> fire(HasEnterHandlers<T> source, WizardProxy<T> proxy, String previousStep)
	{
		EnterEvent<T> event = new EnterEvent<T>(proxy, previousStep);
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(EnterHandler<T> handler)
	{
		handler.onEnter(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Type<EnterHandler<T>> getAssociatedType()
	{
		return (Type) getType(this.resource.getClass());
	}
	
	public String getPreviousStep()
    {
    	return previousStep;
    }	
}
