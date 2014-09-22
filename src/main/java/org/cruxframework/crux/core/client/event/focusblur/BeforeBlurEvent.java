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
package org.cruxframework.crux.core.client.event.focusblur;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeBlurEvent extends GwtEvent<BeforeBlurHandler> implements BeforeFocusOrBeforeBlurEvent
{
	private static Type<BeforeBlurHandler> TYPE = new Type<BeforeBlurHandler>();

	private boolean canceled;

	/**
	 * 
	 */
	protected BeforeBlurEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<BeforeBlurHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static BeforeBlurEvent fire(HasBeforeBlurHandlers source)
	{
		BeforeBlurEvent event = new BeforeBlurEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(BeforeBlurHandler handler)
	{
		handler.onBeforeBlur(this);
	}

	@Override
	public Type<BeforeBlurHandler> getAssociatedType()
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
	 * @see org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusOrBeforeBlurEvent#cancel()
	 */
	public void cancel()
	{
		canceled = true;
	}
}
