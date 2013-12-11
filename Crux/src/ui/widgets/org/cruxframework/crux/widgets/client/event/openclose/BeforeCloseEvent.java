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
package org.cruxframework.crux.widgets.client.event.openclose;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeCloseEvent extends GwtEvent<BeforeCloseHandler> implements BeforeOpenOrBeforeCloseEvent
{
	private static Type<BeforeCloseHandler> TYPE = new Type<BeforeCloseHandler>();

	private boolean canceled;

	/**
	 * 
	 */
	protected BeforeCloseEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<BeforeCloseHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static BeforeCloseEvent fire(HasBeforeCloseHandlers source)
	{
		BeforeCloseEvent event = new BeforeCloseEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(BeforeCloseHandler handler)
	{
		handler.onBeforeClose(this);
	}

	@Override
	public Type<BeforeCloseHandler> getAssociatedType()
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

	/* (non-Javadoc)
	 * @see org.cruxframework.crux.widgets.client.event.openclose.BeforeOpenOrBeforeCloseEvent#cancel()
	 */
	public void cancel()
	{
		canceled = true;
	}
}
