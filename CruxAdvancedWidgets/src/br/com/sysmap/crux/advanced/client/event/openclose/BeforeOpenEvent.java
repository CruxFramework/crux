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
package br.com.sysmap.crux.advanced.client.event.openclose;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeOpenEvent extends GwtEvent<BeforeOpenHandler> implements BeforeOpenOrBeforeCloseEvent
{
	private static Type<BeforeOpenHandler> TYPE = new Type<BeforeOpenHandler>();

	private boolean canceled;

	protected BeforeOpenEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<BeforeOpenHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static BeforeOpenEvent fire(HasBeforeOpenHandlers source)
	{
		BeforeOpenEvent event = new BeforeOpenEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(BeforeOpenHandler handler)
	{
		handler.onBeforeOpen(this);
	}

	@Override
	public Type<BeforeOpenHandler> getAssociatedType()
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
	 * @see br.com.sysmap.crux.advanced.client.event.openclose.BeforeOpenOrBeforeCloseEvent#cancel()
	 */
	public void cancel()
	{
		canceled = true;
	}
}
