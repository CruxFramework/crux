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
package br.com.sysmap.crux.widgets.client.event.collapseexpand;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeExpandEvent extends GwtEvent<BeforeExpandHandler> implements BeforeCollapseOrBeforeExpandEvent
{
	private static Type<BeforeExpandHandler> TYPE = new Type<BeforeExpandHandler>();

	private boolean canceled;

	/**
	 * Creates a new before selection event.
	 */
	protected BeforeExpandEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<BeforeExpandHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static BeforeExpandEvent fire(HasBeforeExpandHandlers source)
	{
		BeforeExpandEvent event = new BeforeExpandEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(BeforeExpandHandler handler)
	{
		handler.onBeforeExpand(this);
	}

	@Override
	public Type<BeforeExpandHandler> getAssociatedType()
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
	 * Cancel the before selection event.
	 */
	public void cancel()
	{
		canceled = true;
	}
}
