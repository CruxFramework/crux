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



/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class EnterEvent<T extends Serializable> extends StepEvent<EnterHandler<T>, T> 
{
	private final String previousStep;
	
	/**
	 * 
	 */
	protected EnterEvent(WizardProxy<T> wizardProxy, String previousStep)
	{
		super(wizardProxy);
		this.previousStep = previousStep;
	}

	/**
	 * @return
	 */
	public static <T extends Serializable> Type<EnterHandler<T>> getType(EnterHandler<T> handler)
	{
		return new Type<EnterHandler<T>>();
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

	/**
	 * @param <T>
	 * @param source
	 * @param wizardId
	 * @param previousStep
	 * @return
	 */
	public static <T extends Serializable> EnterEvent<T> fire(HasEnterHandlers<T> source, String wizardId, String previousStep)
	{
		return fire(source, new PageWizardProxy<T>(wizardId), previousStep);
	}

	@Override
	protected void dispatch(EnterHandler<T> handler)
	{
		handler.onEnter(this);
	}

	@Override
	public Type<EnterHandler<T>> getAssociatedType()
	{
		return new Type<EnterHandler<T>>();
	}
	
	public String getPreviousStep()
    {
    	return previousStep;
    }	
}
