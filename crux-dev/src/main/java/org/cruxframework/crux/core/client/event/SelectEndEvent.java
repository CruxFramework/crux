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
package org.cruxframework.crux.core.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SelectEndEvent extends GwtEvent<SelectEndHandler>
{
	private static Type<SelectEndHandler> TYPE = new Type<SelectEndHandler>();
	
	/**
	 * 
	 */
	public SelectEndEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<SelectEndHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SelectEndHandler handler)
	{
		handler.onSelectEnd(this);
	}

	@Override
	public Type<SelectEndHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> SelectEndEvent fire(HasSelectEndHandlers source) 
	{
		if (TYPE != null) 
		{
			SelectEndEvent event = new SelectEndEvent();
			source.fireEvent(event);
			return event;
		}
		return null;
	}
}