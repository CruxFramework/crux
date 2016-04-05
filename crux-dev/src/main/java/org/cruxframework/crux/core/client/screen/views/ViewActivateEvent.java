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

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.event.shared.GwtEvent;

public class ViewActivateEvent extends GwtEvent<ViewActivateHandler>
{
	private static Type<ViewActivateHandler> TYPE;
	
	private final Object parameter;
	private View view;

	/**
	 * 
	 */
	protected ViewActivateEvent(View view, Object parameter)
	{
		this.view = view;
		this.parameter = parameter;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final Type<ViewActivateHandler> getAssociatedType()
	{
		return (Type) TYPE;
	}

	@SuppressWarnings("unchecked")
    public <T> T getParameterObject()
	{
		return (T) parameter;
	}

	public View getView()
	{
		return view;
	}

	@Override
	protected void dispatch(ViewActivateHandler handler)
	{
		handler.onActivate(this);
	}

	public static void fire(View source, Object parameter)
	{
		if (TYPE != null)
		{
			ViewActivateEvent event = new ViewActivateEvent(source, parameter);
			source.fireActivateEvent(event);
		}
	}

	public static Type<ViewActivateHandler> getType()
	{
		return TYPE != null ? TYPE : (TYPE = new Type<ViewActivateHandler>());
	}

	@Deprecated
	@Legacy
	public String getSenderId()
    {
	    return view.getId();
    }
}
