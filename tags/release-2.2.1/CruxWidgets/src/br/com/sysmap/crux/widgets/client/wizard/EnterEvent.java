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



/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class EnterEvent extends StepEvent<EnterHandler> 
{
	private static Type<EnterHandler> TYPE = new Type<EnterHandler>();

	private final String previousStep;
	
	/**
	 * 
	 */
	protected EnterEvent(WizardProxy wizardProxy, String previousStep)
	{
		super(wizardProxy);
		this.previousStep = previousStep;
	}

	/**
	 * @return
	 */
	public static Type<EnterHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static EnterEvent fire(HasEnterHandlers source, WizardProxy proxy, String previousStep)
	{
		EnterEvent event = new EnterEvent(proxy, previousStep);
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(EnterHandler handler)
	{
		handler.onEnter(this);
	}

	@Override
	public Type<EnterHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public String getPreviousStep()
    {
    	return previousStep;
    }	
}
