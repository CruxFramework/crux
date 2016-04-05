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

public class ViewDeactivateEvent extends GwtEvent<ViewDeactivateHandler>
{
	private static Type<ViewDeactivateHandler> TYPE;

	private boolean canceled = false;
	private View view;

	/**
	 * 
	 */
	protected ViewDeactivateEvent(View view)
	{
		this.view = view;
	}

	public void cancel()
	{
		this.canceled = true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final Type<ViewDeactivateHandler> getAssociatedType()
	{
		return (Type) TYPE;
	}
	
	public View getView()
	{
		return view;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCanceled()
    {
	    return this.canceled;
    }
	
	@Override
	protected void dispatch(ViewDeactivateHandler handler)
	{
		handler.onDeactivate(this);
	}

	public static ViewDeactivateEvent fire(View source)
	{
		ViewDeactivateEvent event = new ViewDeactivateEvent(source);
		source.fireDeactivateEvent(event);
		return event;
	}

	public static Type<ViewDeactivateHandler> getType()
	{
		return TYPE != null ? TYPE : (TYPE = new Type<ViewDeactivateHandler>());
	}
	
	@Deprecated
	@Legacy
	public String getSenderId()
    {
	    return view.getId();
    }
}
