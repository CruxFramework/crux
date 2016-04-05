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

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewUnloadEvent extends GwtEvent<ViewUnloadHandler>
{
	private static Type<ViewUnloadHandler> TYPE;

	private boolean canceled = false;
	private View view;

	/**
	 * 
	 */
	protected ViewUnloadEvent(View view)
	{
		this.view = view;
	}

	public void cancel()
	{
		this.canceled = true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final Type<ViewUnloadHandler> getAssociatedType()
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
	protected void dispatch(ViewUnloadHandler handler)
	{
		handler.onUnload(this);
	}

	public static ViewUnloadEvent fire(View source)
	{
		ViewUnloadEvent event = new ViewUnloadEvent(source);
		source.fireUnloadEvent(event);
		return event;
	}

	public static Type<ViewUnloadHandler> getType()
	{
		return TYPE != null ? TYPE : (TYPE = new Type<ViewUnloadHandler>());
	}
	
	@Deprecated
	@Legacy
	public String getSenderId()
    {
	    return view.getId();
    }
}
