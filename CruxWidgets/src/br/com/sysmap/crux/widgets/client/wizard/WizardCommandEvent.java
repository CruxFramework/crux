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
public class WizardCommandEvent<T extends Serializable> extends StepEvent<WizardCommandHandler<T>, T> 
{
	private static Map<Class<?>, Type<WizardCommandHandler<?>>> TYPES = new HashMap<Class<?>, Type<WizardCommandHandler<?>>>();

	private T resource;
	
	/**
	 * 
	 */
	protected WizardCommandEvent(WizardProxy<T> wizardProxy)
	{
		super(wizardProxy);
		if (wizardProxy != null)
		{
			this.resource = wizardProxy.getResource();
		}
	}

	/**
	 * @return
	 */
	public static Type<WizardCommandHandler<?>> getType(Class<?> clazz)
	{
		if (!TYPES.containsKey(clazz)) 
		{
			TYPES.put(clazz, new Type<WizardCommandHandler<?>>());
		}

		return TYPES.get(clazz);
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static <T extends Serializable> WizardCommandEvent<T> fire(HasWizardCommandHandlers<T> source, WizardProxy<T> proxy)
	{
		WizardCommandEvent<T> event = new WizardCommandEvent<T>(proxy);
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(WizardCommandHandler<T> handler)
	{
		handler.onCommand(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Type<WizardCommandHandler<T>> getAssociatedType()
	{
		return (Type) getType(this.resource.getClass());
	}
}
