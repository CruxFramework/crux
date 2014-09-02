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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.screen.views.View;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ChangeViewEvent extends GwtEvent<ChangeViewHandler>
{
	private static Type<ChangeViewHandler> TYPE = new Type<ChangeViewHandler>();
	private final View previous;
	private final View next;

	/**
	 * @param next 
	 * @param previous 
	 * 
	 */
	public ChangeViewEvent(View previous, View next)
	{
		this.previous = previous;
		this.next = next;
	}

	/**
	 * @return
	 */
	public static Type<ChangeViewHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeViewHandler handler)
	{
		handler.onChangeView(this);
	}

	@Override
	public Type<ChangeViewHandler> getAssociatedType()
	{
		return TYPE;
	}

	public View getPreviousView()
    {
    	return previous;
    }

	public View getNextView()
    {
    	return next;
    }

	public static <T> void fire(HasChangeViewHandlers source, View previous, View next) 
	{
		if (TYPE != null) 
		{
			ChangeViewEvent event = new ChangeViewEvent(previous, next);
			source.fireEvent(event);
		}
	}	
}