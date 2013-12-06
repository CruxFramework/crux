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
package org.cruxframework.crux.widgets.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class FinishEvent extends GwtEvent<FinishHandler> 
{
	private static Type<FinishHandler> TYPE = new Type<FinishHandler>();

	private boolean canceled;

	/**
	 * 
	 */
	protected FinishEvent()
	{
		super();
	}

	/**
	 * @return
	 */
	public static Type<FinishHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static FinishEvent fire(HasFinishHandlers source)
	{
		FinishEvent event = new FinishEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(FinishHandler handler)
	{
		handler.onFinish(this);
	}

	@Override
	public Type<FinishHandler> getAssociatedType()
	{
		return TYPE;
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
}
