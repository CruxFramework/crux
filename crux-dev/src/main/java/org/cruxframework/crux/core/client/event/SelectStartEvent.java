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
public class SelectStartEvent extends GwtEvent<SelectStartHandler>
{
	private static Type<SelectStartHandler> TYPE = new Type<SelectStartHandler>();
	
	/**
	 * 
	 */
	public SelectStartEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<SelectStartHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SelectStartHandler handler)
	{
		handler.onSelectStart(this);
	}

	@Override
	public Type<SelectStartHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> SelectStartEvent fire(HasSelectStartHandlers source) 
	{
		if (TYPE != null) 
		{
			SelectStartEvent event = new SelectStartEvent();
			source.fireEvent(event);
			return event;
		}
		return null;
	}
}