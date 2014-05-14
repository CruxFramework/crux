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
package org.cruxframework.crux.widgets.client.slider;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlidingEvent extends GwtEvent<SlidingHandler>
{
	private static Type<SlidingHandler> TYPE = new Type<SlidingHandler>();

	private boolean movementStarted;
	
	/**
	 * 
	 */
	protected SlidingEvent(boolean movementStarted)
	{
		this.movementStarted = movementStarted;
	}

	/**
	 * @return
	 */
	public static Type<SlidingHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SlidingHandler handler)
	{
		handler.onSliding(this);
	}

	@Override
	public Type<SlidingHandler> getAssociatedType()
	{
		return TYPE;
	}

	public boolean isMovementStarted()
	{
		return movementStarted;
	}
	
	public static <T> void fire(HasSlidingHandlers source, boolean movementStarted) 
	{
		if (TYPE != null) 
		{
			SlidingEvent event = new SlidingEvent(movementStarted);
			source.fireEvent(event);
		}
	}	
}