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
public class LeaveEvent<T extends Serializable> extends StepEvent<LeaveHandler<T>, T> 
{
	private boolean canceled;
	private final String nextStep;

	/**
	 * 
	 */
	protected LeaveEvent (WizardProxy<T> wizardProxy, String nextStep)
	{
		super(wizardProxy);
		this.nextStep = nextStep;
	}

	/**
	 * @return
	 */
	public static <T extends Serializable> Type<LeaveHandler<T>> getType(LeaveHandler<T> handler)
	{
		return new Type<LeaveHandler<T>>();
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

	/**
	 * @param <T>
	 * @param source
	 * @param wizardId
	 * @param nextStep
	 * @return
	 */
	public static <T extends Serializable> LeaveEvent<T> fire(HasLeaveHandlers<T> source, String wizardId, String nextStep)
	{
		return fire(source, new PageWizardProxy<T>(wizardId), nextStep);
	}

	@Override
	protected void dispatch(LeaveHandler<T> handler)
	{
		handler.onLeave(this);
	}

	@Override
	public Type<LeaveHandler<T>> getAssociatedType()
	{
		return new Type<LeaveHandler<T>>();
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
