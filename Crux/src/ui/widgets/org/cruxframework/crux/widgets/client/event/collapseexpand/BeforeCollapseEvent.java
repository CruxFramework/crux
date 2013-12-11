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
package org.cruxframework.crux.widgets.client.event.collapseexpand;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeCollapseEvent extends GwtEvent<BeforeCollapseHandler> implements BeforeCollapseOrBeforeExpandEvent
{
	private static Type<BeforeCollapseHandler> TYPE = new Type<BeforeCollapseHandler>();

	private boolean canceled;

	/**
	 * Creates a new before selection event.
	 */
	protected BeforeCollapseEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<BeforeCollapseHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static BeforeCollapseEvent fire(HasBeforeCollapseHandlers source)
	{
		BeforeCollapseEvent event = new BeforeCollapseEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(BeforeCollapseHandler handler)
	{
		handler.onBeforeCollapse(this);
	}

	@Override
	public Type<BeforeCollapseHandler> getAssociatedType()
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
